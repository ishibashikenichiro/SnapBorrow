# -*- coding: utf-8 -*-
class MemberController < ApplicationController

  # メンバ一覧
  def index
    @members = Member.find_all_by_group_id(session[:group_id])
    @managers = Manager.where(:group_id => session[:group_id]).pluck(:user_id)
  end

  # メンバ管理
  def management
    @members = Member.where(:group_id => session[:group_id])
    @managers = Manager.where(:group_id => session[:group_id]).pluck(:user_id)
    render 'management'
  end

  # 招待ページ
  # @param [Fixnum] g グループ識別ハッシュ
  def invite
    url_hash = params[:g]
    flash[:url_hash] = url_hash
    flash[:invite] = true

    # 該当する招待ログの取得
    log = InvitationLog.find_by_url_hash(url_hash)
    if !log
      flash.now[:message] = "不正な招待です"
      flash[:url_hash] = nil
      flash[:invite] = nil
      render 'failure'
      return
    elsif log.expire_at.to_s < Time.now.to_s
      flash.now[:message] = "有効期限が切れています"
      flash[:url_hash] = nil
      flash[:invite] = nil
      render 'failure'
      return
    end

    flash[:mail] = log.mail
    @group = Group.find(log.group_id)

    if session[:user_id]
      render "invite_confirmation"
      return
    else
      render "invite", :layout =>'invite'
      return
    end
  end

  # 招待承認ページ
  def invite_confirmation
    flash[:url_hash] = flash[:url_hash]
    flash[:invite] = flash[:invite]
    @group = Group.find(InvitationLog.find_by_url_hash(flash[:url_hash]).group_id)

    user_groups = Member.where(:user_id => session[:user_id]).pluck(:group_id)
    if user_groups.include?(@group.id)
      flash.now[:message] = "あなたは既に「#{@group.name}」グループに所属しています。"
      flash[:url_hash] = nil
      flash[:invite] = false
      render 'failure' and return
    end

    render 'invite_confirmation', :layout =>'invite'
    return
  end

  # メンバの追加
  # @param [Fixnum] g グループ識別ハッシュ
  def create
    user_id = session[:user_id]
    url_hash = flash[:url_hash]

    user_groups = Member.where(:user_id => session[:user_id]).pluck(:group_id)

    # 該当する招待ログの取得
    log = InvitationLog.find_by_url_hash(url_hash)
    @group = Group.find(log.group_id)
    if !log
      flash.now[:message] = "不正な招待です。"
      flash[:url_hash] = nil
      flash[:invite] = false
      render 'failure'
      return
    elsif log.expire_at < Time.now
      flash.now[:message] = "有効期限が切れています。"
      flash[:url_hash] = nil
      flash[:invite] = false
      render 'failure'
      return
    elsif user_groups.include?(@group.id)
      flash.now[:message] = "あなたは既に「#{@group.name}」グループに所属しています。"
      flash[:url_hash] = nil
      flash[:invite] = false
      render 'failure' and return
    end

    group_id = log.group_id

    ActiveRecord::Base.transaction do
      member = Member.create(:user_id => user_id, :group_id => group_id)
      log.expire_at = Time.now.strftime("%Y-%m-%d %H:%M:%S")
      log.save!
      flash[:message] = Group.find(group_id).name + "に所属しました。"
      flash[:url_hash] = nil
      flash[:invite] = false
      session[:group_id] = log.group_id

      # 操作ログの作成
      ActionLog.create(:user_id => user_id, :group_id => session[:group_id], :device => "Web", :action => "join group", :date => Time.now.strftime("%Y-%m-%d %H:%M:%S"))
    end

    redirect_to :action => 'invite_success' and return
  end

  # 招待成功ページ
  def invite_success
    render 'success'
  end

  # メンバの削除
  # @param [Fixnum] user_id 削除するユーザID
  # @return [View] マネジメントページ
  def delete
    ActiveRecord::Base.transaction do
      manager = Manager.find_by_user_id_and_group_id(params[:user_id], session[:group_id])
      if manager
        manager.destroy
      end
      Member.find_by_user_id_and_group_id(params[:user_id], session[:group_id]).destroy
    end
    redirect_to :action => 'management'
  end

  # グループからの脱退機能
  # @param [Fixnum] group_id 脱退するグループID
  # @return [View] グループ選択ページ
  def leave
    user_id = session[:user_id]
    group_id = params[:group_id]

    group = Group.find(group_id)
    if LendLog.find_by_user_id_and_book_id_and_return_date(user_id, Book.find_all_by_group_id(group_id), nil)
        flash[:leave_message] = "「#{group.name}」グループで借用中の図書があります。脱退するためには図書を返却をしてください。"
        redirect_to :controller => 'group', :action => 'change_group' and return
    end

    member = Member.find_by_user_id_and_group_id(user_id, group_id)
    members = Member.find_all_by_group_id(group_id)
    managers = Manager.where(:group_id => group_id)
    is_manager = Manager.find_by_user_id_and_group_id(user_id, group_id)


    # そのグループのマネージャの場合
    if member && is_manager
      # そのグループのマネージャが他にいる場合は脱退できる
      if managers.size > 1
        ActiveRecord::Base.transaction do
          member.destroy

          # マネージャの場合はマネージャも辞任
          if is_manager
            is_manager.destroy
          end
        end

      # そのグループのマネージャが他にいなく，メンバが他に存在する場合はエラーを表示
      elsif managers.size == 1 && members.size > 1
        flash[:leave_message] = "「#{group.name}」グループには他のグループマネージャがいません。脱退するためには他のグループメンバをグループマネージャに任命してください。"
        redirect_to :controller => 'group', :action => 'change_group' and return

      # そのグループのメンバが1人しかいない場合はグループ解散することの確認
      elsif members.size == 1
        flash[:breakup_group_id] = group_id
        redirect_to :controller => 'group', :action => 'breakup' and return
      end

    # そのグループのマネージャじゃない場合はそのまま脱退
    elsif member && !is_manager
      ActiveRecord::Base.transaction do
        member.destroy
      end
    end
    flash[:leave_message] = "「#{group.name}」グループから脱退しました。もう一度所属するためには招待を受けてください。"
    redirect_to :controller => 'group', :action => 'change_group' and return
  end


  # グループ解散を含めたグループ脱退機能
  # @param [Fixnum] group_id 脱退するグループID
  # @return [View] グループ選択ページ
  def breakup
    user_id = session[:user_id]
    group_id = params[:group_id]

    is_manager = Manager.find_by_user_id_and_group_id(user_id, group_id)
    ActiveRecord::Base.transaction do
      member = Member.find_by_user_id_and_group_id(user_id, group_id)
      member.destroy

      # マネージャの場合はマネージャも辞任
      if is_manager
        is_manager.destroy
      end
    end
    flash[:leave_message] = "「#{Group.find(group_id).name}」グループを解散しました。"
    redirect_to :controller => 'group', :action => 'change_group' and return
  end

end
