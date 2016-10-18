# SnapBorrow  
”Snap Borrow”は画像による特定物体認識を利用して備品を認識するため，  
これまで備品管理に必要とされたバーコード等の識別コードを利用することなく  
簡便な備品管理を実現する．  
本システムにより利用者はスマートフォンで備品を撮影するだけで対象備品を  
特定し，記録を残しながら容易に備品を登録・貸出・返却することが可能となる．  

環境  
Ubuntu14.04  
Ruby2.0  
Rails3.2  
MySQL5  
OpenCV3  

インストール手順  
1.アップデート  
1.1 $sudo apt-get update  
1.2 $sudo apt-get upgrade -y  

2.Ruby関連  
2.1 $sudo apt-get install ruby2.0  
2.2 $sudo ln -sf /usr/bin/ruby2.0 /usr/bin/ruby  
2.3 $sudo ln -sf /usr/bin/gem2.0 /usr/bin/gem  
2.4 $sudo gem install rails -v '3.2'  
2.5 $sudo bundle install  

3.MySQL5  
$sudo apt-get install mysql mysql-server  

4.ImageMgick  
$sudo apt-get install imagemagick libmagickwand-dev    

5.OpenCV3  
5.1 $sudo apt-get -y install libopencv-dev build-essential cmake git libgtk2.0-dev pkg-config python-dev python-numpy libdc1394-22 libdc1394-22-dev libjpeg-dev libpng12-dev libtiff4-dev libjasper-dev libavcodec-dev libavformat-dev libswscale-dev libxine-dev libgstreamer0.10-dev libgstreamer-plugins-base0.10-dev libv4l-dev libtbb-dev libqt4-dev libfaac-dev libmp3lame-dev libopencore-amrnb-dev libopencore-amrwb-dev libtheora-dev libvorbis-dev libxvidcore-dev x264 v4l-utils unzip  
5.2 $wget https://github.com/Itseez/opencv/archive/3.0.0.zip -O opencv-3.0.0.zip  
5.3 $unzip opencv-3.0.0.zip  
5.4 $cd opencv-3.0.0  
5.5 $mkdir build  
5.6 $cd build  
5.7 $cmake -D CMAKE_BUILD_TYPE=RELEASE -D CMAKE_INSTALL_PREFIX=/usr/local -D WITH_TBB=ON -D WITH_V4L=ON -D OPENCV_EXTRA_MODULES_PATH=./opencv_contrib/modules  
5.8 $make -j $(nproc)  
5.9 $udo make install  
5.10$sudo /bin/bash -c 'echo "/usr/local/lib" > /etc/ld.so.conf.d/opencv.conf'  
5.11$sudo ldconfig  

7.railsの準備  
7.1 $cd server  
7.2 $sudo rake db:create  
7.3 $sudo rake db:migrate  

8.分類器関連の実行ファイルをコンパイル  
8.1 $cd server/recognizer  
8.2 $g++ -v -o reclib libsvm_rec.cpp svm.cpp `pkg-config --cflags --libs opencv`  
8.3 $g++ -v -o makelib bowlibsvm.cpp svm.cpp `pkg-config --cflags --libs opencv`  

9.railsサーバを起動  
9.1 $cd server  
9.2 $sudo rails s -p 80  
