# -*- coding: utf-8 -*-

class UserController < ApplicationController
  # 新規ユーザ
  def new
    @user = User.new
  end

  # ユーザ登録の確認
  # @param [String] :login_id 登録するユーザのログインID
  # @param [String] :nickname 登録するユーザの表示名
  # @param [String] :password 登録するユーザのパスワード
  # @param [String] :mail 登録するユーザのメールアドレス
  # @return [View] 登録画面または登録確認画面
  def confirmation
    if params[:group_id] != nil
      @group = Group.find_by_id(params[:group_id])
    elsif flash[:invite]
      @group = Group.find(InvitationLog.find_by_url_hash(flash[:url_hash]).group_id)
    end

    # 確認画面のために入力されたデータを保持する
    flash[:login_id] = params[:login_id]
    flash[:nickname] = params[:nickname]
    if flash[:mail]
      flash[:mail] = flash[:mail]
    else
      flash[:mail] = params[:mail]
    end
    flash[:invite] = flash[:invite]
    flash[:url_hash] = flash[:url_hash]

    #未入力があるかをチェック
    if (params[:login_id] == "" ||
        params[:nickname] == "" ||
        params[:mail]  == "" ||
        params[:password] == "" ||
        params[:password_confirmation] == "")
      flash[:error] = 1
      if flash[:invite]
        redirect_to :controller => 'member',:action => 'invite', :g => flash[:url_hash] and return
      end
      redirect_to :action => 'new' and return
    end

    #ユーザIDが適切なのかを判断する
    if !(/^[0-9A-Za-z][0-9A-Za-z_\'\-]*$/ =~ params[:login_id]) || params[:login_id].size > 16
      flash[:error] = 6
      if flash[:invite]
        redirect_to :controller => 'member',:action => 'invite', :g => flash[:url_hash] and return
      end
      redirect_to :action => 'new' and return
    end

    #パスワードが適切なのかを判断する
    if params[:password].size > 256 || !(/^[!-~]{8,}$/ =~ params[:password])
      flash[:error] = 5
      if flash[:invite]
        redirect_to :controller => 'member',:action => 'invite', :g => flash[:url_hash] and return
      end
      redirect_to :action => 'new' and return
    end

    #ニックネームが適切なのかを判断する
    if params[:nickname].size > 40
      flash[:error] = 7
      if flash[:invite]
        redirect_to :controller => 'member',:action => 'invite', :g => flash[:url_hash] and return
      end
      redirect_to :action => 'new' and return
    end


    #パスワードとパスワード確認が一致するかを判断する
    if params[:password] != params[:password_confirmation]
      flash[:error] = 4
      if flash[:invite]
        redirect_to :controller => 'member',:action => 'invite', :g => flash[:url_hash] and return
      end
      redirect_to :action => 'new' and return
    end

    is_login_id = User.find_by_login_id(params[:login_id]) ? true : false
    is_mail = User.find_by_mail(params[:mail]) ? true : false

    # メールアドレスが適切かどうか検証する
    if ValidatesEmailFormatOf::validate_email_format(params[:mail])
      flash[:error] = 8
      if flash[:invite]
        redirect_to :controller => 'member',:action => 'invite', :g => flash[:url_hash] and return
      end
      redirect_to :action => 'new' and return
    end

    flash[:login_id] = params[:login_id]
    flash[:nickname] = params[:nickname]
    flash[:mail] = params[:mail]

    if is_login_id
      flash[:error] = 2
      if flash[:invite]
        render 'member/invite' and return
      end
      redirect_to :action => 'new' and return
    elsif is_mail
      flash[:error] = 3
      if flash[:invite]
        render 'member/invite' and return
      end
      redirect_to :action => 'new' and return
    else
      if flash[:invite]
        render 'confirmation', :login_id => params[:login_id], :nickname => params[:nickname], :mail => flash[:mail], :password => params[:password], :layout => "invite" and return
      end
      render 'confirmation', :login_id => params[:login_id], :nickname => params[:nickname], :mail => flash[:mail], :password => params[:password] and return
    end
  end

  #ユーザ登録機能
  def create
    flash[:invite] = flash[:invite]
    flash[:url_hash] = flash[:url_hash]

    @user = User.new(:login_id => params[:login_id],
                     :nickname => params[:nickname],
                     :mail => params[:mail],
                     :password => params[:password])
    if @user.save
      flash[:message] = "ユーザ登録が完了しました。新規グループを作成するか、グループの招待を受けてください"
      session[:group_id] = 0
      session[:user_id] = @user.id
      if flash[:url_hash] && flash[:invite]
        redirect_to :controller =>'member', :action => 'invite_confirmation' and return
      end

      redirect_to :action => 'create_success' and return
    else
      flash.now[:message] = "ユーザ登録ができませんでした。入力内容を確認してください。"
      render 'failure' and return
    end
  end

  # ユーザ登録成功ページ
  def create_success
    render 'success'
  end

  # プロフィールの表示
  # @return [View] プロフィール画面
  def show_profile
     @user = User.find_by_id(session[:user_id])
    render 'profile'
  end

  #借用中図書一覧
  def show_borrowing
    @lend_logs = LendLog.where(:user_id => session[:user_id], :return_date =>  nil)
    @catalogs = BookCatalog.where(:book_code => @lend_logs.pluck(:book_code), :group_id => [0, session[:group_id]])
    render 'borrowing'
  end

  # プロフィール編集画面
  def show_profile_edit
    @user = User.find_by_id(session[:user_id])
    render 'profile_edit'
  end

  # パスワード変更画面
  def show_password_edit
    render 'password_edit'
  end

  # プロフィールの更新
  # @param [String] :password パスワード
  # @param [String] :nickname 表示名
  # @return [View] プロフィール画面
  def edit_profile
    @user = User.find_by_id(session[:user_id])

    @user.nickname = params[:nickname]
    @user.mail = params[:mail]

    if @user.save
      flash.now[:message] = "プロフィールを更新しました。"
      render 'success' and return
    else
      flash.now[:message] = "プロフィールを更新できませんでした。"
      render 'failure' and return
    end
  end

  def edit_password
    @user = User.find_by_id(session[:user_id])

    if !@user.authenticate(params[:password_old])
      flash[:error] = 6
      render 'password_edit' and return
    end

    if params[:password] != params[:password_confirmation]
      flash[:error] = 4
      render 'password_edit' and return
    end

    if params[:password].size < 0 || params[:password].size > 17 || !(/^\w+$/ =~ params[:password])
      flash[:error] = 5
      render 'password_edit' and return
    end


    @user.password = params[:password]

    if @user.save
      flash.now[:message] = "パスワードを変更しました。"
      render :action => 'success' and return
    else
      flash.now[:message] = "パスワードを変更できませんでした。"
      render 'failure' and return
    end
  end

  # パスワードリセットページ
  # @params [String] g 識別用のURLハッシュ値
  # @return [View] パスワードリセット画面
  def password_reset_request
    url_hash = params[:g]
    user = PasswordReset.find_by_url_hash(url_hash)
    if !user
      flash.now[:message] = "不正な変更申請です"
      render 'failure' and return
    else
      if user.expire_at.to_s < Time.now.to_s
        flash.now[:message] = "有効期限が切れています"
        render 'failure' and return
      else
        flash[:url_hash] = url_hash
        render 'password_reset' and return
      end
    end
  end

  # パスワードリセット機能
  # @return [View] パスワードリセット結果画面
  def password_reset
    req = PasswordReset.find_by_url_hash(flash[:url_hash])
    user = User.find(req.user_id)

    if params[:password] != params[:password_confirmation]
      flash[:error] = 4
      flash[:url_hash] = flash[:url_hash]
      render 'password_reset' and return
    end

    if params[:password].size < 0 || params[:password].size > 17 || !(/^\w+$/ =~ params[:password])
      flash[:error] = 5
      flash[:url_hash] = flash[:url_hash]
      render 'password_reset' and return
    end

    user.password = params[:password]

    if user.save
      flash[:message] = "パスワードを変更しました。右上のLoginからログインしてください"
      req.expire_at = Time.now.strftime("%Y-%m-%d %H:%M:%S")
      req.save

      render 'success' and return
    else
      flash[:message] = "パスワードを変更できませんでした。"
      render 'failure' and return
    end
  end

  # パスワードリセット申請ページ
  # @return [View] パスワードリセット申請画面
  def forget_password_request
    render 'forget_password' and return
  end

  # パスワードリセット申請機能
  # @param [String] login_id メールアドレスまたはログインID
  # @return [View] パスワードリセット結果画面
  def forget_password
    login_id = params[:login_id]

    # 登録されていたとき
    if user = User.find_by_mail(login_id) ||  user = User.find_by_login_id(login_id)
      @forget_user = user
      log = PasswordReset.find_all_by_user_id(user.id).last

      # ログがあった場合
      unless log.nil?
        if Time.now.to_s < log.expire_at.to_s
          flash.now[:message] = "既ににメールを送信しています。迷惑メールに入っていないかご確認ください。再び送信する場合は一定時間を空けて申請してください。"
          render 'forget_password' and return
        end
      end

      # URLハッシュ値の生成
      seed = user.mail + Time.now.to_s
      url_hash = Digest::SHA512.hexdigest(seed)
      PasswordReset.create(:user_id => user.id, :url_hash => url_hash, :expire_at => 30.minute.since)

#      UserMailer.forget_password(user.mail, url_hash).deliver
      flash.now[:message] = ""
      render 'send_success' and return
    else
      flash.now[:message] = "登録されていないログインIDまたはメールアドレスです"
      render 'forget_password' and return
    end
  end

  # グループの所属情報や本棚情報の問い合わせ機能
  # @param [Fixnum] user_id ユーザID
  # @param [Fixnum] group_id グループID
  # @return [String] 結果のJSON
  def get_status
    user_id = params[:user_id]
    group_id = params[:group_id]
    has_shelf = 0
    has_group = 0
    has_user = 0

    if Member.find_by_user_id_and_group_id(user_id, group_id)
      has_group = 1
    end

    if Shelf.find_by_group_id(group_id)
      has_shelf = 1
    end

    if User.find_by_id_and_delete_date(user_id, nil)
      has_user = 1
    end

    result = {"has_shelf" => "#{has_shelf}", "has_group" => "#{has_group}", "has_user" => "#{has_user}"}
    render :json => result.to_json
  end

  # ユーザの削除機能
  # @return [View] ログイン画面
  def delete
    user_id = session[:user_id]
    delete_name = params[:delete_name]
    password = params[:password]
    password_confirmation = params[:password_confirmation]

    user = User.find_by_id(user_id)

    if !user.authenticate(password)
      flash[:error] = 9
      redirect_to :action => 'delete_confirmation' and return
    elsif password != password_confirmation
      flash[:error] = 4
      redirect_to :action => 'delete_confirmation' and return
    end


    managers = Manager.find_all_by_user_id(user_id)
    lend_logs = LendLog.find_all_by_user_id_and_return_date(user_id, nil)

    if !managers.empty?
      flash[:user_message] = "マネージャとなっているグループが存在するためユーザを削除できません。該当するグループを脱退してから削除してください。"
      redirect_to :controller => 'user', :action => 'show_profile' and return
    elsif !lend_logs.empty?
      flash[:user_message] = "借用中の図書があるため，ユーザを削除できません。該当する図書を返却してから削除してください。"
      redirect_to :controller => 'user', :action => 'show_profile' and return
    end

    ActiveRecord::Base.transaction do


      # ログインIDとメールアドレスを削除
      user.login_id = nil
      user.mail = nil
      user.delete_date = Time.now.to_s(:datetime)

      # パスワードをランダムな254文字に
      password = SecureRandom.hex(127)
      user.password = password
      user.password_confirmation = password

      if delete_name
        user.nickname = "削除されたユーザ"
      end

      user.save!

      # メンバ一覧から削除
      members = Member.find_all_by_user_id(user_id)
      members.each do |member|
        member.destroy
      end

      session[:user_id] = nil
      session[:group_id] = 0
      session[:manager] = nil
    end

    redirect_to :action => 'delete_success'
  end

  # ユーザの削除確認画面
  # @return [View] 確認画面
  def delete_confirmation
    render 'delete_confirmation'
  end

  # ユーザの削除完了画面
  def delete_success
    render 'delete_success'
  end

end
