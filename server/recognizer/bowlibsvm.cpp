#define LIB_SVM
#pragma warning(disable:4996)

#ifdef LIB_SVM
#include "svm.h"
struct svm_parameter param;		// SVM設定用パラメータ
struct svm_problem prob;		// データセット（ラベル＆データ）・パラメータ
struct svm_node *x_space;		// データ・パラメータ（svm_problemの下部変数）
struct svm_model *model;		// 学習データ・パラメータ
#endif //LIB_SVM

#include <opencv2/opencv.hpp>
#include <opencv2/core.hpp>
#include <opencv2/features2d.hpp>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <string>
#include <math.h>
#include <fstream>
#include <vector>
#include <sstream>
#include <algorithm>
#include <exception>

using namespace cv;
using namespace std;


int recognize(vector<string> file_list, vector<int> class_num, string group_id, string dec){

	//ファイル名の設定
	string group_dictionary = dec + "/libdictionary_" + group_id + ".xml";
	string group_svm = dec + "/libsvm_" + group_id + ".xml";

	//アルゴリズムにAKAZEを使用する
	Ptr<FeatureDetector> detector = AKAZE::create(AKAZE::DESCRIPTOR_MLDB, 0, 3, 0.00001f, 4, 20, KAZE::DIFF_PM_G2);
	Mat featuresUnclustered(0, 0, CV_32F);
	Mat features;
	Mat features2;
	vector<KeyPoint> keypoint;

	// BOW特徴抽出器パラメータ設定
	// FeatureDetectorオブジェクト
	Ptr<FeatureDetector> detector_prob = AKAZE::create(AKAZE::DESCRIPTOR_MLDB, 0, 3, 0.00001f, 4, 20, KAZE::DIFF_PM_G2);

	// DescriptionExtractorオブジェクトの生成
	Ptr<DescriptorExtractor> extractor = AKAZE::create(AKAZE::DESCRIPTOR_MLDB, 0, 3, 0.00001f, 4, 20, KAZE::DIFF_PM_G2);

	Ptr<DescriptorMatcher> matcher = DescriptorMatcher::create("BruteForce");

	int clusterCount = 100;//クラスタkの数
	TermCriteria tc(CV_TERMCRIT_ITER, 10, 0.001);
	int attempts = 3;
	int flags = KMEANS_PP_CENTERS;

	BOWKMeansTrainer bowtrainer(clusterCount, tc, attempts, flags);
	BOWImgDescriptorExtractor bowDE(extractor, matcher);

	// dataの各画像から局所特徴量を抽出
	//特徴ベクトルを取り出し、学習用DBを作る
	for (int n = 0; n < (int)file_list.size(); n++) {
		Mat img = imread(file_list[n], 0);
		if (img.empty()) {
			cerr << "Error: Could not open one of the images." << endl;
		}

		detector->detect(img, keypoint);
		detector->compute(img, keypoint, features);
		featuresUnclustered.push_back(features);
	}
	//辞書の作成
	Mat featuresUnclusteredF(featuresUnclustered.rows, featuresUnclustered.cols, CV_32F);
	featuresUnclustered.convertTo(featuresUnclusteredF, CV_32F);
	Mat dictionary = bowtrainer.cluster(featuresUnclusteredF);
	Mat dictionary_svm(dictionary.rows, dictionary.cols, CV_8U);
	dictionary.convertTo(dictionary_svm, CV_8U);
	resize(dictionary_svm, features2, cv::Size(), 2, 1);
	bowDE.setVocabulary(dictionary_svm);
	//辞書のファイルへの保存
	FileStorage cvfs2(group_dictionary, CV_STORAGE_WRITE);
	write(cvfs2, "dictionary", dictionary_svm);
	cvfs2.release();
	//SVM学習データ
#ifdef LIB_SVM
	// 学習する識別器のパラメータ
	svm_parameter param;

	// SVM設定値
	param.svm_type = C_SVC;// SVC
	param.kernel_type = RBF;// RBF（放射基底関数）カーネル
	param.degree = 3;
	param.gamma = 5.0;// カーネルなどで使われるパラメータ
	param.coef0 = 0;
	param.nu = 0.5;
	param.cache_size = 100;
	param.C = 1.0;// 一定量以下の誤りを容認するソフトマージン化のためのペナルティ係数C:小さいほどソフトマージン
	param.eps = 1e-3;
	param.p = 0.1;
	param.shrinking = 1;
	param.probability = 1;//可能性の情報を得る
	param.nr_weight = 0;
	param.weight_label = NULL;
	param.weight = NULL;

	//学習データ
	svm_problem prob;

	// データセットのパラメータ設定
	prob.l = file_list.size();	// データセット数

	// 各パラメータのメモリ領域確保
	prob.y = new double[prob.l];		// ラベル
	prob.x = new svm_node *[prob.l];		// データセットの分だけデータ収納空間を作成
	x_space = new svm_node[(clusterCount + 1)*prob.l];

	// データセット・パラメータへの数値入力
	for (int m = 0; m < prob.l; m++){
		Mat test_img = imread(file_list[m], 0);
		if (test_img.empty()) {
			cerr << "Error: Could not open one of the images." << endl;
		}
		Mat test_bowDescriptor;
		vector<KeyPoint> test_keypoint;
		detector->detect(test_img, test_keypoint);
		bowDE.compute(test_img, test_keypoint, test_bowDescriptor);

		prob.y[m] = class_num[m];//ラベル

		for (int j = 0; j<clusterCount; j++){
			x_space[(clusterCount + 1)*m + j].index = j + 1;			// データ番号の入力
			x_space[(clusterCount + 1)*m + j].value = test_bowDescriptor.at<float>(0, j);	// データ値の入力 
		}
		x_space[(clusterCount + 1)*m + clusterCount].index = -1;
		prob.x[m] = &x_space[(clusterCount + 1)*m];	// prob.xとx_spaceとの関係付
	}

	//学習
	cout << "Ready to train ..." << endl;
	svm_model *model = svm_train( &prob, &param );
	cout << "Finished ..." << endl;

	//分類器の保存
	svm_save_model(group_svm.c_str(), model);

	//識別

	// 後始末
	svm_free_and_destroy_model(&model);
	svm_destroy_param(&param);
	// メモリ領域の開放
	delete[] prob.y;
	delete[] prob.x;
	delete[] x_space;

#else//libsvm未使用
	Mat flagmat(file_list.size(), 1, CV_32SC1);
	Mat datamat = Mat(file_list.size(), clusterCount, CV_32FC1) * 0;

	for (int m = 0; m < (int)file_list.size(); m++) {
		Mat test_img = imread(file_list[m], 0);
		if (test_img.empty()) {
			cerr << "Error: Could not open one of the images." << endl;
		}
		Mat test_bowDescriptor;
		vector<KeyPoint> test_keypoint;
		detector->detect(test_img, test_keypoint);
		bowDE.compute(test_img, test_keypoint, test_bowDescriptor);
		for (int i = 0; i < clusterCount; i++){
			datamat.at<float>(m, i) = test_bowDescriptor.at<float>(0, i);
		}
		for (int p = 0; p < (int)class_list.size(); p++){
			int loc = file_list[m].find(class_list[p], 0);
			if (loc != string::npos)
				flagmat.at<int>(m, 1) = p;
		}
	}
	//svmの設定
	SVM svm;
	SVMParams svmParams;
	// SVMのパラメータを設定
	svmParams.svm_type = SVM::C_SVC;    // C_SVC, NU_SVC, ONE_CLASS, EPS_SVR, NU_SVR
	svmParams.kernel_type = SVM::RBF;   // LINEAR, POLY, RBF, SIGMOID
	svmParams.C = 1.0;	//一定量以下の誤りを容認するソフトマージン化のためのペナルティ係数C
	svmParams.gamma = 5.0;
	//svmParams.degree = 10.0;
	//分類機の作成と保存
	svm.train(datamat, flagmat, Mat(), Mat(), svmParams);
	svm.save("/home/sit-user-15/recognizertest/svm_image_test.xml");
#endif//LIB_SVM

	return 0;

}

int main(int argc, char *argv[]){
	string group_id = argv[1];
	string dec = argv[2];

	vector<int> class_num;
	int n = 0;
	string file_name;
	vector<string> file_list;
	for (int m = 0; m < argc; m++){
		cout << m << ":" << argv[m] << endl;
	}

	// \dataの中のファイル名をfile_listに格納する
	for (int i = 3; i < argc; i++){
		file_name = argv[i];
		file_list.push_back(file_name);
		i++;
		n = atoi(argv[i]);
		class_num.push_back(n);;//class_numtにclass名を格納
		cout << file_name << endl;
		cout << i << endl;
		cout << argc << endl;
	}

	if (class_num.size() > 1){
		cout << "making recognizer" << endl;
		recognize(file_list, class_num, group_id, dec);
		cout << "finish" << endl;
	}
	return 0;

}