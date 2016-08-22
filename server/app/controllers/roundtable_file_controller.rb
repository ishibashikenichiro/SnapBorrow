class RoundtableFileController < ApplicationController
  def upload
		if  session[:user_id].blank?
			@error = "ログインしたユーザーが不明です。"
			render  'failure'
			return
		end
		
		#if　
		 
	#	end


		if params[:roundtable_id].blank? || params[:title].blank? || params[:upload].blank? 
			@error = "入力に不備があります。"
			render  'failure'
			return
		else 
  			data=params[:upload]
			uptime=DateTime.now
			uploadfilename=data[:datafile].original_filename
			ext=File.extname(uploadfilename)
			
			if uploadfilename.downcase.end_with?('.sh','.exe','.rb', '.rhtml','.erb','.php','.asp','.aspx','.pl','.py')
				@error = "アップロード不可のファイルタイプです。"
				render  'failure'
				return
			end
			if data[:datafile].size > 10485760
				@error = "ファイルのサイズが制限を超えたのでアップロードできません。"
				render  'failure'
				return
			end

			newuptest=RoundtableFile.new(:filename=>uploadfilename, :title=>params[:title])
			newuptest.roundtable=Roundtable.find(params[:roundtable_id])
			newuptest.user=User.find(session[:user_id])
			newuptest.uploadtime=uptime
			newuptest.save!
			newfilename=newuptest.id.to_s + "_"+ newuptest.uploadtime.strftime("%Y%m%d%H%M%S")+ext
			File.open(Rails.root.join('roundtable_file_uploads', newfilename), 'wb') do |of|
    				of.write(data[:datafile].read)
 		end
		end
		redirect_to :back
  		#redirect_to :action => "index"
  end

  def download
		downfile=RoundtableFile.find_by_id(params[:file_id])
		if downfile.blank?
			@error = "ダウンロードする輪講資料の記録がありません。"
			render  'failure'
			return
		end
		if session[:user_id].blank?
			@error = "ログインしないとダウンロードできません。"
			render  'failure'
			return
		end
		member=Member.find_by_group_id_and_user_id(downfile.roundtable[:group_id], session[:user_id])
		if member.blank?
			@error = "輪講を行うグループのメンバーでないとダウンロードできません。"
			render  'failure'
			return
		end
		downfilename=downfile.id.to_s + "_"+ downfile.uploadtime.strftime("%Y%m%d%H%M%S")+File.extname(downfile.filename)
		downfilepath=Rails.root.join('roundtable_file_uploads', downfilename).to_s
		if File.exist?(downfilepath)
			send_file(downfilepath, :filename=>downfile.filename)
		else
			@error = "該当するファイルが見つかりませんでした。"
			render  'failure'
			return
		end
  end

  def delete
		deletefile=RoundtableFile.find_by_id(params[:file_id])
		if deletefile.blank?
			@error = "削除する輪講資料の記録がありません。"
			render  'failure'
			return
		end	
	
		manager=Manager.find_by_group_id_and_user_id(deletefile.roundtable[:group_id], session[:user_id])
		if deletefile[:user_id] != session[:user_id] && manager.blank?
			@error = "作成者またはグループマネジャーでないと削除できません。"
			render  'failure'
			return
		end		

		delfilename=deletefile.id.to_s + "_"+ deletefile.uploadtime.strftime("%Y%m%d%H%M%S")+File.extname(deletefile.filename)
		if File.exist?(Rails.root.join('roundtable_file_uploads', delfilename).to_s)
			File.delete(Rails.root.join('roundtable_file_uploads', delfilename).to_s)
			deletefile.destroy
		end
		redirect_to :back
		#redirect_to :controller=:action => "index"
  end
end
