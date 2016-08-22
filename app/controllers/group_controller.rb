# -*- coding: utf-8 -*-
class GroupController < ApplicationController
  protect_from_forgery :except => ['get_groups']

  # グループ詳細表示
  def show
    @group = Group.find_by_id(Member.find(session[:user_id]))
  end

  # グループ作成成功ページ
  def create_success
    render 'success'
  end

  # グループ作成機能
  # @param [String] :group_name グループの名前
  # @return [View] 成功または失敗画面
  def create
    if params[:group_name].size > 40 || params[:group_name].size < 1
      flash.now[:message] = "グループ名のフォーマットが間違っています。"
      render 'new' and return
    end
    @group = Group.new(:name => params[:group_name])

    if @group.save
      Manager.create(:group_id => @group.id, :user_id => session[:user_id])
      Member.create(:group_id => @group.id, :user_id => session[:user_id])
      flash[:message] = "『#{@group.name}』を登録しました。現在は『#{@group.name}』です。"
      flash[:group_id] = @group.id
      session[:group_id] = @group.id
      session[:manager] = true

      # 操作ログの作成
      ActionLog.create(:user_id => session[:user_id], :group_id => session[:group_id], :device => "Web", :action => "create group", :date => Time.now.strftime("%Y-%m-%d %H:%M:%S"))

      redirect_to :action => 'create_success' and return
    else
      flash.now[:message] = "『#{@group.name}』の登録に失敗しました。"
      render 'failure' and return
    end
  end

  # グループ切り替え画面
  # @return [View] マネジメントページ
  def change_group
    @group_ids = Member.where(:user_id => session[:user_id])
    render 'change_group'
  end

  # グループの切替機能
  # @param [Fixnum] :group_id 切り替えるグループID
  # @return [View] ホーム画面
  def change
    if params[:group_id].class == Array
      session[:group_id] = params[:group_id][0].to_i
    else
      session[:group_id] = params[:group_id].to_i
    end
    if Manager.find_by_user_id_and_group_id(session[:user_id], session[:group_id])
      session[:manager] = true
    else
      session[:manager] = false
    end

    ActiveRecord::Base.transaction do
      ActionLog.create(:user_id => session[:user_id], :group_id => session[:group_id], :device => "Web", :action => "group change", :date => Time.now.strftime("%Y-%m-%d %H:%M:%S"))
    end

    redirect_to '/'
  end

  # Androidのグループ一覧取得機能
  # @param [Fixnunm] :user_id 取得するユーザID
  # @return [String] グループ一覧の配列を含むJSON形式の文字列
  def get_groups
    user_id = params[:user_id]
    group_ids = []
    group_names = []

    # ユーザIDの所属するグループID一覧を取得
    Member.find_all_by_user_id(user_id).each do |group|
      group_ids.push(group.group_id)
    end

    #  ユーザIDの所属するグループ名一覧を取得
    group_ids.each do |id|
      group_names.push(Group.find(id).name)
    end

    render :json => array_to_json(group_ids, group_names)
  end

  # グループ招待ページ
  def invite
        #ドメインの取得
    $domain = root_url(:only_path => false).to_s
    render 'invite'
    return
  end

  # グループ招待メールの送信
  def invite_mail
    group_id = session[:group_id]
    mails = []

    # フォームのアドレスの取得
    10.times do |i|
      m = params["mail" + (i + 1).to_s]
      if m != ""
          mails.push(m)
      end
    end


    # メールの送信
    mails.each do |to|
      # URLハッシュ値の生成
      seed = to + Time.now.to_s
      url_hash = Digest::SHA512.hexdigest(seed)

      ActiveRecord::Base.transaction do
        # 招待ログの生成
        InvitationLog.create(:group_id => group_id, :mail =>to, :expire_at => 1.day.since, :url_hash => url_hash)
        # メールの送信
        InvitationMailer.sendmail_invite(to, url_hash).deliver
      end
    end

    flash[:message] = "招待メールを送信しました。"
    redirect_to :action => 'invite'
  end

  # グループ解散する場合の確認画面
  def breakup
    if flash[:breakup_group_id]
      @group = Group.find(flash[:breakup_group_id])
      flash[:breakup_group_id] = flash[:breakup_group_id]
    else
      @group = Group.find(session[:group_id])
    end

    render 'breakup'
  end

  # グループIDの一覧のJSON作成
  # @param [Array] group_ids グループIDの配列
  # @param [Array] group_names グループ名の配列
  # @return [String] 結果と図書コードを含むJSON形式の文字列
  def array_to_json(group_ids, group_names)
    json_array = {}
    result_array = Array.new
    result_array[0] = {"group_ids" => group_ids}
    result_array[1] = {"group_names" => group_names}
    json_array["result"] = result_array
    result = JSON.generate(json_array)
    return result
  end
end
