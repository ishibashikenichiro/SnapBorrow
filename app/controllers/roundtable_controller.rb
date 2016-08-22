class RoundtableController < ApplicationController
	def view
		if  session[:user_id].blank?
			@error = "未ログインです。"
			render  'failure'
			return
		end
		if  params[:id].blank? == false
			viewbyid
			return
		end
		if  params[:book_code].blank?
			@error = "図書コードが不明です。"
			render  'failure'
			return
		end

		@book_code=params[:book_code]
                @round_table
		#@roundtables=Roundtable.find(:book_code=>@book_code)
		book=Book.exists?(:book_code => @book_code, :group_id =>session[:group_id])
		if book==false
			@error="グループにない図書です。"
			render 'failure'
			return
		end
		if Roundtable.exists?(:book_code => @book_code, :group_id => session[:group_id])  == false
			render 'createfrombook'
			return
		end
		@roundtables = Roundtable.where(:book_code=>params[:book_code], :group_id => session[:group_id])
	end

	def createfrombook
		@book_code=params[:book_code]
		@title=params[:title]
	end

	def create
		@output=""
		@output2=""
		@createok=0
		unless session[:group_id] == 0 
			check_empty = Roundtable.find_by_book_code_and_group_id(params[:book_code],  session[:group_id])
			if check_empty.nil? 
				rttitle=BookCatalog.find_by_book_code(params[:book_code]).title
				ActiveRecord::Base.transaction do
					newroundtable = Roundtable.new( :book_code=>params[:book_code])
					if params[:title].blank?
						newroundtable.title=rttitle
					else
						newroundtable.title=params[:title]
					end
					newroundtable.group = Group.find(session[:group_id])
					@output=newroundtable[:title]
					newroundtable.save!
					@output2="輪講を登録しました。"
					@createok=1
				end
			end
		else
			@output="グループに加入しないと登録できません。"
		end
		
	end
	def new
		currentTime = Time.now
		if flash[:starttime].blank?
			flash[:starttime] = currentTime.strftime("%Y年%m月%d日%H時%M分")
		end 
		if flash[:endtime].blank?
			flash[:endtime] = currentTime.strftime("%Y年%m月%d日%H時%M分")
		end 
	end
	def edit
		if  session[:user_id].blank?
			@error = "未ログインです。"
			render  'failure'
			return
		end
		if  params[:id].blank?
			@error = "不明な輪講IDです。"
			render  'failure'
			return
		end
		if Roundtable.exists?(:id => params[:id], :group_id=>session[:group_id] )  == false
			@error = "不明な輪講IDです。"
			render  'failure'
			return			
		end
		roundtable = Roundtable.where(:id=>params[:id]).first
		flash[:roundtableid] = params[:id]
		flash[:title]=roundtable.title
		flash[:place]=roundtable.place
				currentTime = Time.now
		
		if roundtable.startdatetime == nil or roundtable.enddatetime == nil
			 @time_undetermined=1
		else
			 flash[:starttime] = roundtable.startdatetime.strftime("%Y年%m月%d日%H時%M分")
			 flash[:endtime] = roundtable.enddatetime.strftime("%Y年%m月%d日%H時%M分")
		end
		
		@allmembers=Member.find_all_by_group_id(session[:group_id])
		@presenters=RoundtablePresenter.find_all_by_roundtable_id(params[:id])
		@presenters.each do |presenter|
			@allmembers.delete_if{|member| member.user_id==presenter.user_id}
		end

	end
	def edit_save
		@output=""
		@output2=""
		@createok=0
		noupdatetime=0

		if  params[:id].blank?
			@output = "不明な輪講IDです。"
			return
		end
		if Roundtable.exists?(:id => params[:id], :group_id=>session[:group_id] )  == false
			@output = "不明な輪講IDです。"
			return			
		end
		if params[:title].blank?
			@output = "輪講タイトルが空欄です"
			return
		end
		
		if params[:notime].blank? or params[:notime] != "notime" 
			begin
				teststarttime = Time.strptime(params[:starttime],"%Y年%m月%d日%H時%M分")
				testendtime = Time.strptime(params[:endtime],"%Y年%m月%d日%H時%M分")
			rescue
				@output = "入力日時が不正です"
			return
			end
		else
			noupdatimetime=1
		end

		unless session[:group_id] == 0  
			ActiveRecord::Base.transaction do
				newroundtable = Roundtable.find_by_id(params[:id])
				newroundtable.title=params[:title]
				if noupdatetime !=1 
					newroundtable.startdatetime = teststarttime
					newroundtable.enddatetime = testendtime
				end
				newroundtable.place = params[:place]
				@output=newroundtable[:title]
				if newroundtable.save
					@output2="輪講情報を更新しました。"
					@createok=1
					@newroundtableid=newroundtable.id
				else
					@output="輪講情報の更新が失敗しました。"
				end
			end
		else
			@output="グループに加入しないと更新できません。"
		end
		
	end
	def new_confirm
		flash[:title] = params[:title]
		flash[:starttime] = params[:starttime]
		flash[:endtime] = params[:endtime]
		flash[:place] = params[:place]
	end
	def new_save
		@output=""
		@output2=""
		@createok=0
		if params[:title].blank?
			@output = "輪講タイトルが空欄です"
			return
		end
		begin
			teststarttime = Time.strptime(params[:starttime],"%Y年%m月%d日%H時%M分")
			testendtime = Time.strptime(params[:endtime],"%Y年%m月%d日%H時%M分")
		rescue
			@output = "入力日時が不正です"
			return
		end
		unless session[:group_id] == 0  
			rttitle=params[:title]
			ActiveRecord::Base.transaction do
				newroundtable = Roundtable.new( :title=>params[:title])
				newroundtable.group = Group.find(session[:group_id])
				newroundtable.startdatetime = teststarttime
				newroundtable.enddatetime = testendtime
				newroundtable.place = params[:place]
				@output=newroundtable[:title]
				if newroundtable.save
					@output2="輪講を登録しました。"
					@createok=1
					@newroundtableid=newroundtable.id
				else
					@output="輪講情報の編集が失敗しました。"
				end
			end
		else
			@output="グループに加入しないと登録できません。"
		end
	end

	def viewbyid
		if  session[:user_id].blank?
			@error = "未ログインです。"
			render  'failure'
			return
		end
		if  params[:id].blank?
			@error = "不明な輪講IDです。"
			render  'failure'
			return
		end
		if Roundtable.exists?(:id => params[:id])  == false
			@error = "存在しない輪講IDです。"
			render  'failure'
			return
		end
		@roundtables = Roundtable.where(:id=>params[:id])
		render 'view'
		return
	end
	def index
		case params[:sort]
			when "1"
				@roundtables=Roundtable.where(:group_id => session[:group_id]).order("updated_at desc")
				@sort=1
			when "2"
				@roundtables=Roundtable.where(:group_id => session[:group_id]).order(:startdatetime)
				@sort=2
			when "3"
				@roundtables=Roundtable.where(:group_id => session[:group_id]).order(:title)
				@sort=3
			else
				@roundtables=Roundtable.where(:group_id => session[:group_id]).order("created_at desc")
				@sort=0
		end	
	end
	def add_presenter
		if  session[:user_id].blank?
			@error = "未ログインです。"
			render  'failure'
			return
		end
		member = Member.find_by_user_id(session[:user_id])
		logingroupid=member.group_id
		if Roundtable.find_by_id_and_group_id(params[:roundtable_id],logingroupid).blank?
			@error = "グループに加入していません。"
			render  'failure'
			return		
		end
		if  params[:newpresenter].blank? or params[:roundtable_id].blank?
			@error = "必要のデータが未入力です。"
			render  'failure'
			return
		end
		if RoundtablePresenter.find_by_user_id_and_roundtable_id(params[:newpresenter], params[:roundtable_id]).blank? == false
			flash[:add_presenter_error] =1
			redirect_to :back
			return
		end

		presenter=RoundtablePresenter.new
		presenter.roundtable_id=params[:roundtable_id]
		presenter.user_id=params[:newpresenter]
		if presenter.save
			redirect_to :back
		else
			@error = "輪講発表者の追加が失敗しました。"
			render  'failure'	
		end	
	end

	def delete_presenter
		if  session[:user_id].blank?
			@error = "未ログインです。"
			render  'failure'
			return
		end
		member = Member.find_by_user_id(session[:user_id])
		logingroupid=member.group_id
		if Roundtable.find_by_id_and_group_id(params[:roundtable_id],logingroupid).blank?
			@error = "グループに加入していません。"
			render  'failure'
			return		
		end
		if  params[:presenter].blank? or params[:roundtable_id].blank?
			@error = "必要のデータが未入力です。"
			render  'failure'
			return
		end

		presenters=RoundtablePresenter.where(:user_id=>params[:presenter], :roundtable_id=>params[:roundtable_id])
		if presenters.blank?
			@error = "削除する輪講発表者の記録がありません。"
			render  'failure'
			return
		end
		
		presenters.each do |presenter|
			if presenter.destroy
			else
				@error = "輪講発表者の削除が失敗しました。"
				render  'failure'	
			end	
		end
		redirect_to :back
	end

end
