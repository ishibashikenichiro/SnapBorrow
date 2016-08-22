# -*- coding: utf-8 -*-
class LendLogController < ApplicationController
  protect_from_forgery :except => ['create']

  # 借用ログ一覧
  def index
    books = Book.find_all_by_group_id(session[:group_id])
    @lend_logs = LendLog.where(:book_id => books).order("lend_date ASC")
    @catalogs = BookCatalog.where(:group_id => [0, session[:group_id]])
  end

  # 一時借用ログを作る
  # @param [Fixnum] :group_id 登録するグループID
  # @param [Fixnum] :user_id 登録するユーザID
  # @param [String] :book_code 登録する図書コード
  # @return [String] 結果と図書のタイトルを含むJSON形式の文字列
  def temporary_create
    group_id = params[:group_id]
    user_id = params[:user_id]
    book_code = params[:book_code]

    ActiveRecord::Base.transaction do
      # 借用者の数を取得
      lenders = 0
      logs = []
      books = Book.find_all_by_book_code_and_group_id_and_delete_date(book_code, group_id, nil)
      books.each do |book|
        logs.push(LendLog.find_all_by_book_id_and_return_date(book.id, nil))
      end
      lenders = logs.size

      # 一時借用ログを作成
      # status 一覧
      # 0:一時借用ログ作成時
      # 1:誰かがNoを押した場合
      # 2:全員がYesを押した場合
      # -1:借用ログを作成済み
      temporary_lend_log = TemporaryLendLog.create(:book_code => book_code, :user_id => user_id, :group_id => group_id, :lend_date => Time.now.strftime("%Y-%m-%d %H:%M:%S"), :status => 0, :lenders => lenders, :possessors => 0)

      render :json => array_to_json(1, BookCatalog.find_by_book_code_and_group_id(book_code, [0, group_id]).title)
    end
  end

  # 借用ログを作る
  # @param [Fixnum] :group_id 登録するグループID
  # @param [Fixnum] :user_id 登録するユーザID
  # @param [String] :book_code 登録する図書コード
  # @return [String] 結果と図書のタイトルを含むJSON形式の文字列
  # 結果一覧
  # 0:返却
  # 1:借用
  # -1:すべての図書が借用されていた場合
  # -2:図書が登録されていなかった場合
  # -3:グループに所属していなかった場合
  # -4:該当する図書が複数の本棚にあった場合
  # -5:該当する図書の本棚を登録する必要がある場合
  def create
    group_id = params[:group_id]
    user_id = params[:user_id]
    book_code = params[:book_code]

    # 該当する借用ログがあるかどうか
    is_lendlog = false

    # 該当する一時借用ログがあるかどうか
    is_temporary = false

    # 一時借用ログが返却済みかどうか
    is_return = false

    # 未借用の図書があるかどうか
    is_book = false

    # 図書が登録されているかどうか
    is_book_register = false

    # 借用したい図書のタイトル
    title = ""

    # 該当する図書の一時借用ログの一覧
    temporary_logs = []

    # 該当する借用ログ
    lend_log = ""

    # 該当する一時借用ログ
    temp_first = ""

    # グループに所属しているかどうか
    unless Member.find_by_user_id_and_group_id(user_id, group_id)
      render :json => array_to_json(-3, book_code)
      return
    end

    # タイトルの取得
    catalog =  BookCatalog.find_by_book_code_and_group_id(book_code, [0, group_id])
    if catalog
      title = catalog.title
    else
      title = book_code
    end

    # グループ内のbook_codeを持つ図書一覧の取得
    books_group = Book.find_all_by_book_code_and_group_id_and_delete_date(book_code, group_id, nil)

    # 図書がある場合
    if !books_group.empty?
      is_book_register = true

      books_group.each do |book|
        if book.state == false
          is_book = true
        end
      end

      # 一時借用ログがあるかどうか
      tmp = TemporaryLendLog.where(:book_code => book_code, :group_id => group_id, :user_id => user_id, :status => [0, 1])
      unless tmp.empty?
        tmp.each do |log|
          if log.status != -1
            is_temporary = true
            temporary_logs.push(log)
            break
          end
        end
      end

      if !temporary_logs.empty?
        temp_first = temporary_logs.first
        # 一時借用ログが返却済みかどうか
        if temp_first.return_date != nil
          is_return = true
        end
      end

      # 借用ログがあるかどうか
      lend_log = LendLog.find_by_book_code_and_user_id_and_return_date(book_code, user_id, nil)
      if lend_log
        is_lendlog = true
      end

      # 図書が登録されていなかった場合
    else
      shelves, shelves_name = get_shelves(group_id)
      render :json => array_to_json_shelves(-2, book_code, shelves, shelves_name)
      return
    end

    ActiveRecord::Base.transaction do
      # 借用ログに一致した場合
      if is_lendlog

        shelves, shelves_name = get_shelves(group_id)

        # 一時借用ログがあるかどうか
        tmp_log = TemporaryLendLog.find_all_by_book_code_and_group_id(book_code, group_id)
        tmp_log.each do |log|
          if log.status != -1
            temp_first = log
            break
          end
        end

        # 返却処理
        if temp_first == ""
          lend_log.return_date = Time.now.strftime("%Y-%m-%d %H:%M:%S")
          lend_log.save!
          book = Book.find(lend_log.book_id)
          book.state = false
          book.save!

          # 本棚登録する必要がある場合
          if lend_log.temporary
            render :json => array_to_json_shelves(-5, title, shelves, shelves_name)
            return
          end

          # 一時借用から本棚登録をする必要があるもの
          # または本棚IDが0になってしまっているもの
          if book.shelf_id == 0
            render :json => array_to_json_shelves(-5, title, shelves, shelves_name)
            return
          end

          shelf_name = Shelf.find_by_shelf_id_and_group_id(Book.find(lend_log.book_id).shelf_id, group_id).name
          render :json => array_to_json_shelf_name(0, title, shelf_name)
          return

        # 一時借用ログがある場合
        elsif temp_first.status == 0
          lend_log.return_date = Time.now.strftime("%Y-%m-%d %H:%M:%S")
          lend_log.save!
          book = Book.find(lend_log.book_id)
          book.state = false
          book.save!

          # その人以外が全員図書を所持している場合(図書の登録)
          if temp_first.lenders == (temp_first.possessors + 1)
            temp_first.status = -1
            temp_first.save!

            shelf_id = 0
            book = Book.create(:book_code => book_code, :group_id => group_id, :shelf_id => shelf_id, :state => true)
            LendLog.create(:book_id => book.id, :user_id => temp_first.user_id, :lend_date => temp_first.lend_date, :return_date => temp_first.return_date, :book_code => book_code, :temporary => true)
            if temp_first.return_date != nil
              book.state = false
              book.save!
              render :json => array_to_json_shelves(-5, title, shelves, shelves_name)
              return
            end

          end
          shelf_name = Shelf.find_by_shelf_id_and_group_id(Book.find(lend_log.book_id).shelf_id, group_id).name
          render :json => array_to_json_shelf_name(0, title, shelf_name)
          return

          # 図書を返却して借用ログを作成する場合
        elsif temp_first.status == 1
          temp_first.status = -1
          temp_first.save!

          lend_log.return_date = Time.now.strftime("%Y-%m-%d %H:%M:%S")
          lend_log.save!

          LendLog.create(:book_id => lend_log.book_id, :user_id => temp_first.user_id, :lend_date => temp_first.lend_date, :return_date => temp_first.return_date, :book_code => book_code)
          if temp_first.return_date != nil
            book = Book.find_by_book_code_and_group_id_and_state_and_delete_date(book_code, group_id, true, nil)
            book.state = false
            book.save!
          end

          shelf_name = Shelf.find_by_shelf_id_and_group_id(Book.find(lend_log.book_id).shelf_id, group_id).name
          render :json => array_to_json_shelf_name(0, title, shelf_name)
          return
        end

        # 一時借用ログに一致する場合
      elsif is_temporary

        # 返却済みの場合
        if is_return

          # 図書がある場合は通常借用
          if is_book
            book = Book.find_by_book_code_and_group_id_and_state_and_delete_date(book_code, group_id, false, nil)
            book.state = true
            book.save!

            LendLog.create(:book_id => book.id, :user_id => user_id, :lend_date => Time.now.strftime("%Y-%m-%d %H:%M:%S"), :book_code => book_code)
            render :json => array_to_json(1, title)
            return

            # 図書がない場合は一時借用ログを作成する場合へ
          else
            render :json => array_to_json(-1, title)
            return
          end

          # 返却の場合
        elsif temp_first.status == 0
          temp_first.return_date = Time.now.strftime("%Y-%m-%d %H:%M:%S")
          temp_first.status = -1
          temp_first.save!

          log = LendLog.where(:book_code => book_code, :user_id => Member.where(:group_id => group_id).pluck(:user_id), :return_date => nil).first

          ActiveRecord::Base.transaction do
            book = nil
            # 未返却の借用ログがある場合は図書を追加せずに借用ログを生成する
            if log
              book = Book.find(log.book_id)
              book.state = false
              book.save!
              log.return_date = temp_first.lend_date
              log.save!
              @gcm = GCM.new(ENV['GCM_KEY'])
              notify_cancel(group_id, log)

            # 返却済みの借用ログがある場合は図書を追加して借用ログを生成する
            else
              shelf_id = Book.find_by_book_code_and_group_id_and_state_and_delete_date(book_code, group_id, false, nil).shelf_id
              book = Book.create(:book_code => book_code, :group_id => group_id, :shelf_id => shelf_id, :state => false)
            end
            LendLog.create(:book_id => book.id, :user_id => temp_first.user_id, :lend_date => temp_first.lend_date, :return_date => temp_first.return_date, :book_code => book_code)
          end

          shelf_id = shelf_id ? shelf_id : Book.find_by_book_code_and_group_id_and_delete_date(book_code, group_id, nil).shelf_id
          shelf_name = Shelf.find_by_shelf_id_and_group_id(shelf_id, group_id).name
          render :json => array_to_json_shelf_name(0, title, shelf_name)
          return
        end

      # 一時借用ログを作る場合
      elsif !is_temporary && is_book_register && !is_lendlog && !is_book
        render :json => array_to_json(-1, title)
        return

      # 借用処理
      else
        @book = ""
        books = Book.find_all_by_book_code_and_group_id_and_state_and_delete_date(book_code, group_id, false, nil)
        if books.size > 1
          shelves = []
          shelves_name = []
          books.each do |book|
            if book.shelf_id != 0
              if !shelves.include?(book.shelf_id)
                shelves.push(book.shelf_id)
                shelves_name.push(Shelf.find_by_shelf_id_and_group_id(book.shelf_id, group_id).name)
              end
            end
          end

          if shelves.size > 1
            render :json => array_to_json_shelves(-4, title, shelves, shelves_name)
            return
          else
            @book = books.first
          end
        else
          @book = books.first
        end
        @book.state = true
        @book.save!

        LendLog.create(:book_id => @book.id, :user_id => user_id, :lend_date => Time.now.strftime("%Y-%m-%d %H:%M:%S"), :book_code => book_code)
        render :json => array_to_json(1, title)
        return
      end
    end
  end


  # 本棚を選択して借用する場合
  # @param [Fixnum] qr 本棚ID
  # @param [String] book_code 図書コード
  # @param [Fixnum] group_id グループID
  # @param [Fixnum] user_id ユーザID
  def select_shelf_lend
    shelf_id = params[:qr]
    book_code = params[:book_code]
    group_id = params[:group_id]
    user_id = params[:user_id]

    ActiveRecord::Base.transaction do
      book = Book.find_by_book_code_and_group_id_and_shelf_id_and_state_and_delete_date(book_code, group_id, shelf_id, false, nil)
      book.state = true
      book.save!
      LendLog.create(:book_code => book_code, :book_id => book.id, :user_id => user_id, :lend_date => Time.now.strftime("%Y-%m-%d %H:%M:%S"))
      title = BookCatalog.find_by_book_code_and_group_id(book_code, [0, group_id]).title
      render :json => array_to_json(1, title)
      return
    end
  end

  # 図書を持っている場合
  # @param [String] book_code
  # @param [Fixnum] group_id
  def push_possess
    book_code = params[:book_code]
    group_id = params[:group_id]

    # 一時借用ログの取得
    temporary_log = TemporaryLendLog.find_by_book_code_and_group_id_and_status(book_code, group_id, 0)

    # 一時借用ログがあった場合
    if temporary_log
      temporary_log.possessors += 1

      # 借用者数と所持者数が一緒の場合, その状態を記録しそのまま登録
      if temporary_log.possessors == temporary_log.lenders
        temporary_log.status = -1
        temporary_log.save!

        shelf_id = 0
        book = Book.create(:book_code => book_code, :group_id => group_id, :shelf_id => shelf_id, :state => false)
        # 一時借用ログが借用中の場合
        if temporary_log.return_date == nil
          book.state = true
          book.save!
        end
        LendLog.create(:book_id => book.id, :user_id => temporary_log.user_id, :lend_date => temporary_log.lend_date, :return_date => temporary_log.return_date, :book_code => book_code, :temporary => true)
      end

    end
    render :nothing => true
  end

  # 図書を持っていない場合
  # @param [String] book_code
  # @param [Fixnum] group_id
  def push_not_possess
    book_code = params[:book_code]
    group_id = params[:group_id]

    # 一時借用ログの取得
    temporary_log = TemporaryLendLog.find_by_book_code_and_group_id_and_status(book_code, group_id, 0)

    # 一時借用ログがあった場合, 未所持者がいたことの状態を記録
    if temporary_log
      temporary_log.status = 1
      temporary_log.save!
    end
    render :nothing => true
  end

  # 本棚IDと本棚名の取得
  def get_shelves(group_id)
    shelves = []
    shelves_name = []

    shelf = Shelf.find_all_by_group_id(group_id)
    shelf.each do |sh|
      shelves.push(sh.shelf_id)
      shelves_name.push(sh.name)
    end

    return shelves, shelves_name
  end

  # 借用時のJSON作成
  # @param [Fixnum] status 結果の状態
  # @param [String] book_code 図書コード
  # @return [String] 結果と図書コードを含むJSON形式の文字列
  def array_to_json(status, book_code)
    json_array = {}
    result_array = Array.new
    result_array[0] = {"status" => "#{status}"}
    result_array[1] = {"book_code" => "#{book_code}"}
    json_array["result"] = result_array
    result = JSON.generate(json_array)
    return result
  end

  # 本棚選択のためのJSON作成
  # @param [Fixnum] status 結果の状態
  # @param [String] book_code 図書コード
  # @param [Array] shelves 本棚IDの配列
  # @return [String] 結果と図書コードと本棚IDを含むJSON形式の文字列
  def array_to_json_shelves(status, book_code, shelves, shelf_name)
    json_array = {}
    result_array = Array.new
    result_array[0] = {"status" => "#{status}"}
    result_array[1] = {"book_code" => "#{book_code}"}
    result_array[2] = {"shelves" => shelves}
    result_array[3] = {"shelf_name" => shelf_name}
    json_array["result"] = result_array
    result = JSON.generate(json_array)
    return result
  end


  # 返却時のJSON作成
  # @param [Fixnum] status 結果の状態
  # @param [String] book_code 図書コード
  # @param [String] shelf_name 本棚名
  # @return [String] 結果と図書コードと本棚名を含むJSON形式の文字列
  def array_to_json_shelf_name(status, book_code, shelf_name)
    json_array = {}
    result_array = Array.new
    result_array[0] = {"status" => "#{status}"}
    result_array[1] = {"book_code" => "#{book_code}"}
    result_array[2] = {"shelf_name" => "#{shelf_name}"}
    json_array["result"] = result_array
    result = JSON.generate(json_array)
    return result
  end


  # GCMの通知削除
  # @param [Integer] group_id
  # @param [LendLog] lend_log
  def notify_cancel(group_id, lend_log)
    # registration_idは配列で渡す
    registration_ids = []

    registration_ids.push(DeviceId.find_by_user_id(lend_log.user_id).registration_id)

    title = BookCatalog.find_by_book_code(lend_log.book_code).title
    group_name = Group.find(group_id).name
    shelf = Shelf.find_by_shelf_id_and_group_id(Book.find(lend_log.book_id).shelf_id, group_id)
    # GCMで送るデータの作成
    options = { :data => { 'message' => {
          :title => title,
          :book_code => lend_log.book_code,
          :group_id => group_id,
          :group_name => group_name,
          :shelf_id => shelf.shelf_id,
          :shelf_name => shelf.name,
          :borrow_date => lend_log.lend_date.to_s,
          :auto_return => "true"
        },
        :delay_while_idle => true,
        :collapse_key => "notify_cancel"
      }}

    # GCMで通知
    @gcm.send_notification(registration_ids, options)
  end

end
