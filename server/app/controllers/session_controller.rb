# -*- coding: utf-8 -*-
class SessionController < ApplicationController
  protect_from_forgery :except => ['android_login', 'android_logout']

  # ホーム画面
  def index
  end

  # Webアプリケーションのログイン機能
  # @param [String] :login_id ログインIDまたはメールアドレス
  # @param [String] :password パスワード
  # @param [Fixnum] :group_id グループID
  # @return [View] ログイン成功またはログイン画面
  def create
    flash[:url_hash] = flash[:url_hash]
    flash[:invite] = flash[:invite]
    login_id = params[:login_id]
    password = params[:password]
    group_id = params[:group_id]
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
      else
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

  # ログアウト機能
  # @return [View] ログイン画面
  def logout

    # グループに所属していなければnilに
    if session[:group_id] == 0
      session[:group_id] = ""
    end

    # 操作ログを保存
    ActionLog.create(:user_id => session[:user_id], :group_id => session[:group_id], :device => "Web", :action => "logout", :date => Time.now.strftime("%Y-%m-%d %H:%M:%S"))

    # セッションの破棄
    session[:user_id] = nil
    session[:group_id] = nil
    session[:manager] = nil

    # ログイン画面に戻る
    redirect_to :action => 'login'
  end

  # ログイン画面を表示
  def login
    render 'login'
  end

  # Androidのログイン機能
  # @param [String] :login_id ログインIDまたはメールアドレス
  # @param [String] :password パスワード
  # @return [String] 結果, ユーザID, グループIDを含むJSON形式の文字列
  def android_login
    login_id = params[:login_id]
    password = params[:password]
    group_id = nil


    # 該当するユーザの検索
    user = User.find_by_login_id(login_id)

    pass = false
    # パスワードが適切か判別
    if /^[!-~]{8,256}$/ =~ password
      pass = true
    end

    # ユーザが存在した場合, パスワードを照合
    if user && pass && user.authenticate(password)
      # 最後の操作ログからgroup_idを取得
      action_log = ActionLog.find_all_by_user_id(user.id)
      if action_log.last != nil
        group_id = Member.find_by_group_id_and_user_id(action_log.last.group_id, user.id) ? action_log.last.group_id : nil

      else
        # 初回ログイン時は最初のgroup_idを取得
        # ログイン時のグループを取得
        group = Member.find_all_by_user_id(user.id).first

        # 所属するグループが無い場合はnilにしておく
        group_id = group ? group.group_id : nil
      end

      # グループに所属していたらグループ名を取得
      group_name = group_id ? Group.find(group_id).name : ""

      # 操作ログの作成
      ActionLog.create(:user_id => user.id, :group_id => group_id, :device => "Android", :action => "login", :date => Time.now.strftime("%Y-%m-%d %H:%M:%S"))
      # 所属するグループがない場合は0を返す
      group_id = group_id || 0

      # user_idとログイン成功を返す
      result = login_data_json(1, user.id, group_id, group_name)
    else
      # ログイン失敗を返す
      result = login_data_json(-1, -1, group_id, group_name)
    end
    render :json => result
  end

  # Androidログアウト機能
  # @param [Fixnum] :group_id グループID
  # @param [Fixnum] :user_id ユーザID
  def android_logout
    group_id = params[:group_id]
    user_id = params[:user_id]

    # 操作ログの作成
    ActionLog.create(:user_id => user_id, :group_id => group_id, :device => "Android", :action => "logout", :date => Time.now.strftime("%Y-%m-%d %H:%M:%S"))

    render :nothing => true
  end


  # Androidログイン機能の返却値であるjsonを作る
  # @param [Fixnum] flag 結果を判定するフラグ
  # @param [Fixnum] user_id 該当するユーザのユーザID
  # @param [Fixnum] group_id 該当するユーザのグループID
  def login_data_json(flag, user_id, group_id, group_name)
    json_array = {}
    result_array = Array.new
    result_array[0] = {"flag" => "#{flag}"}
    result_array[1] = {"user_id" => "#{user_id}"}
    result_array[2] = {"group_id" => "#{group_id}"}
    result_array[3] = {"group_name" => "#{group_name}"}
    json_array["result"] = result_array
    result = JSON.generate(json_array)
    return result
  end

end
