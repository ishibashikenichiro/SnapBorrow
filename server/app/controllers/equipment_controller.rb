class EquipmentController < ApplicationController
  # GET /equipment
  # GET /equipment.json
  require 'open3'
  require 'rexml/document'
  before_filter :rec
  def rec
    getpicdec = "*.jpg"
    #newfile = Recognizepicname.find_by_user_id(session[:user_id], :order => "updated_at DESC", :limit => 1)
    file = Dir.glob(Rails.root.join('recognition').join(getpicdec)).sort_by {|f| File.mtime(f)}.reverse[0]
    puts file
    @equipment_check = EquipmentClass.find_all_by_group_id(session[:group_id])
    if @equipment_check.length > 1
      recpicdec =  "reclib " + file + " " + session[:group_id].to_s + " " + Rails.root.join('recognizer').to_s
      o, e, s = Open3.capture3(Rails.root.join('recognizer').join(recpicdec).to_s)
      @str = o
      @ft = file
    end
  end
  
  def rec_img
     @rec_img = Rails.root.join('recognition', params[:filename])
     send_file @rec_img, :disposition =>'inline', :type =>'image/jpeg'
  end

  def index
    Dir.chdir(Rails.root.join('recognition'))
    file2 = Dir.glob("*.jpg").sort_by {|f| File.mtime(f)}.reverse[0]
    puts file2
    @ft2 = file2
    @rec_img = Rails.root.join('recognition', @ft2)
    if @equipment_check.length == 0
      @test = "このグループは備品を所持していません"
      @equipment_ary = EquipmentClass.find_all_by_group_id(session[:group_id])
    elsif @equipment_check.length == 1
      @equipment = EquipmentClass.find_by_group_id(session[:group_id])
      @test = @equipment.equipment_classes_name
      @equipment_id = @equipment.id
      @equipment_ary = EquipmentClass.find_all_by_group_id(session[:group_id])
      @brURL = "/equipment/new?id=" + @equipment_id.to_s
    else
      strAry = @str.split(",")
      @newary = strAry
      @equipment = EquipmentClass.find_by_id(@newary[0])
      @equipment_ary = Array.new
      @newary.each do |num|
        e_ary = EquipmentClass.find_by_id(num)
        @equipment_ary.push(e_ary)
      end

      @test = @equipment.equipment_classes_name
      @equipment_id = @equipment.id
      @brURL = "/equipment/new?id=" + @equipment_id.to_s
    end
    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @equipment }
    end
  end

  def index_android
    flash[:url_hash] = flash[:url_hash]
    flash[:invite] = flash[:invite]
    login_id = params[:login_id]
    password = params[:password]
    group_id = params[:group_id]
    flag = params[:list_flag]
    session[:group_id] = nil
    if flash[:invite]
      @group = Group.find(InvitationLog.find_by_url_hash(flash[:url_hash]).group_id)
    end

    # パスワードがフォーマットにあっているかどうかのフラグ
    pass = false

    # 該当するユーザの検索
    user = User.find_by_login_id(login_id)

    # パスワードが適切か判別
    if /^[!-~]{8,256}$/ =~ password
      pass = true
    end

    # ユーザが存在した場合, パスワードを照合
    if user && pass && user.authenticate(password)

      # user_idをセッションに保存
      session[:user_id] = user.id

      # 送られてきたグループが存在し所属しているかどうか
      if group_id
        group_id = Group.find_by_id(group_id) ? group_id : nil
        if group_id
          group_id = Member.find_by_group_id_and_user_id(group_id, user.id) ? group_id : nil
        end
      end

      # group_idが渡されてきた場合
      if group_id
        session[:group_id] = group_id
        session[:web_view] = true

      # 最後の操作ログからgroup_idを取得
      else
        action_log = ActionLog.find_all_by_user_id(user.id)
        if action_log.last != nil
          if action_log.last.group_id
            # そのグループから削除されていたらnil
            group_id = Member.find_by_group_id_and_user_id(action_log.last.group_id, user.id) ? action_log.last.group_id : nil
            session[:group_id] = group_id
          end
        end

        # 初回ログイン時または削除されていたら最初のgroup_idを取得
        unless session[:group_id]
          # ユーザがグループに所属していれば1つ目のgroup_idを取得し, セッションに保存
          member = Member.find_by_user_id(user.id)
          if member
            session[:group_id] = member.group_id
          else
            session[:group_id] = nil
          end
        end
      end

      # 管理者ならばmanagerに1を入れる
      if (Manager.find_by_user_id_and_group_id(user.id, session[:group_id])) != nil
        session[:manager] = 1
      else
        session[:manager] = nil
      end

      # 操作ログの作成
      ActionLog.create(:user_id => user.id, :group_id => session[:group_id], :device => "Web", :action => "login", :date => Time.now.strftime("%Y-%m-%d %H:%M:%S"))

      # 所属するグループがない場合は0を返す
      if session[:group_id] == nil
        session[:group_id] = 0
      end

      session[:expires_at] = 1.day.from_now
      # ログイン成功画面の表示
      if flash[:invite]
        redirect_to :controller =>'member', :action => 'invite_confirmation'
      elsif flag == '0'
        redirect_to :action => 'show'
      elsif flag == '1'
        redirect_to :action => 'index'
      end
    else

      # ログイン失敗を通知してログイン画面に戻る
      if flash[:invite]
        redirect_to :controller => 'member',:action => 'invite', :login_id => params[:login_id], :error => true, :g => flash[:url_hash] and return
      else
        redirect_to :action => 'login', :login_id => params[:login_id], :error => true and return
      end
    end
    
  end  

  
  # GET /equipment/1
  # GET /equipment/1.json
  def show
    @equipment_list = EquipmentClass.find_all_by_group_id(session[:group_id])
  end

  # GET /equipment/new
  # GET /equipment/new.json
  def new
    @id = params[:id]
    @equipment = Equipment.new
    @e_id = @id.to_i
    @br_equipment_class = EquipmentClass.find_by_id(@e_id)
    @br_equipment = Equipment.find_all_by_equipment_class_id(@e_id)

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @equipment }
    end
  end

  def borrow
    br = Equipment.find_by_id(params[:eqid])
    day = Time.now
    if br.state == 0
      newlend = EquipmentLog.new(equipment_id: br.id, user_id: session[:user_id], equipment_lend_date: day)
      newlend.save
      br.state = 1
      br.save
    else
      returneq = EquipmentLog.where(:equipment_id => br.id).where(:equipment_return_date => nil).first
      if returneq.user_id == session[:user_id] then
      returneq.equipment_return_date = day
      returneq.save
      br.state = 0
      br.save
      end
    end
    redirect_to :back
  end


 def  oneupload
	name=params[:NAME]
        userid=params[:USERID]
        groupid=params[:GROUPID]
	puts "File received NAME="+ name
      data=params[:file]
	@str2 = data
      uptime=DateTime.now
      uploadfilename=data.original_filename
      savepic = Recognizepicname.new(group_id: groupid, user_id: userid, picname: Rails.root.join('recognition').join(uploadfilename).to_s)
      savepic.save

      File.open(Rails.root.join('recognition',uploadfilename),'wb') do |of|
                               of.write(data.read)
  end
 end
  # GET /equipment/1/edit
  def edit
    @equipment = Equipment.find(params[:id])
  end

  def logs
    @lend_log = EquipmentLog.all
  end  

  # POST /equipment
  # POST /equipment.json
  def create
  end

  # PUT /equipment/1
  # PUT /equipment/1.json
  def update
  end

  # DELETE /equipment/1
  # DELETE /equipment/1.json
  def destroy
    @equipment = Equipment.find(params[:id])
    @equipment.destroy

    respond_to do |format|
      format.html { redirect_to equipment_index_url }
      format.json { head :no_content }
    end
  end
end
