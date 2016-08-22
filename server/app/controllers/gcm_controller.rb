# -*- coding: utf-8 -*-
class GcmController < ApplicationController
  before_filter :init
  protect_from_forgery :except => ['register_user_id', 'unregister_user_id']

  # GCMインスタンスの初期化
  def init
    @gcm = GCM.new(ENV['GCM_KEY'])
  end

  # アプリを使わずに返却した場合の借用時に通知する
  # @param [Fixnum] :user_id 借用するユーザID
  # @param [Fixnum] :group_id 借用するユーザのグループID
  # @param [String] :book_code 借用する図書コード
  # @return [String] 通知結果を表示
  def notify
    group_id = params[:group_id]
    book_code = params[:book_code]
    user_id = params[:user_id]

    # registration_idは配列で渡す
    registration_ids = []

    # ログアウト状態のユーザ一覧
    logout_users = []

    # book_codeの図書一覧を取得
    books = Book.where(:book_code => book_code, :group_id => group_id, :delete_date => nil).pluck(:id)

    # 該当する図書の借用ログの一覧
    lend_logs = []

    lend_logs = LendLog.where(:book_id => books, :return_date => nil)

    title = BookCatalog.find_by_book_code(book_code).title
    group_name = Group.find(group_id).name
    # registration_idの一覧を取得
    lend_logs.each do |log|
      registration_id = DeviceId.find_all_by_user_id(log.user_id)
      # ログインしている場合
      unless registration_id.empty?
        registration_id.each do |id|
          registration_ids.push(id.registration_id)
        end

        shelf = Shelf.find_by_shelf_id_and_group_id(Book.find(log.book_id).shelf_id, group_id)
        # GCMで送るデータの作成
        options = { :data => { 'message' => {
              :title => title,
              :book_code => log.book_code,
              :group_id => group_id,
              :group_name => group_name,
              :shelf_id => shelf.shelf_id,
              :shelf_name => shelf.name,
              :borrow_date => log.lend_date.to_s,
              :auto_return => "false"
            },
            :delay_while_idle => true,
            :collapse_key => "notify"
          }}

        # GCMで通知
        res = @gcm.send_notification(registration_ids, options)
        logger.debug(res)

        # ログインしている場合
        NotificationLog.create(:user_id => log.user_id, :message => options.to_s, :date => Time.now.strftime("%Y-%m-%d %H:%M:%S"), :is_notify => true)
        registration_ids = []

      # ログアウトしている場合
      else
        NotificationLog.create(:user_id => log.user_id, :message => options.to_s, :date => Time.now.strftime("%Y-%m-%d %H:%M:%S"), :is_notify => false)
      end
    end

    render :nothing => true
  end

  # ユーザと端末IDの割当
  # @param [Fixnum] :user_id 登録するユーザID
  # @param [String] :reg_id 登録するGCM登録ID
  def register_user_id
    registration_id = params[:reg_id]
    user_id = params[:user_id]

    # registration_idからレコードの取得
    gcm = DeviceId.find_by_registration_id(registration_id)

    # レコードがなかった場合
    if !gcm
      # 新しいレコードの作成
      DeviceId.create(:registration_id => registration_id, :user_id => user_id)
    # レコードが存在し, ユーザが異なる場合
    elsif gcm.user_id != user_id
      gcm.user_id = user_id
      gcm.save
    end
    render :nothing => true
  end

  # ユーザと端末IDの割当の解除
  # @param [String] :reg_id 割当解除するGCM登録ID
  # @param [Fixnum] :user_id 割当解除するユーザID
  def unregister_user_id
    registration_id = params[:reg_id]
    user_id = params[:user_id]

    # 該当するレコードを取得しuser_idを削除
    gcm = DeviceId.find_by_registration_id(registration_id)
    gcm.user_id = nil
    gcm.save

    render :nothing => true
  end
end
