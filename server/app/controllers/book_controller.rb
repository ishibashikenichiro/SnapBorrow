# -*- coding: utf-8 -*-
class BookController < ApplicationController
  protect_from_forgery :except => ['create']

  # 図書一覧画面
  def index
    @books = Book.find_all_by_group_id_and_delete_date(session[:group_id], nil)
    @catalogs = BookCatalog.where(:group_id => [0, session[:group_id]])
  end

  # 図書管理画面
  def management
    @books = Book.find_all_by_group_id(session[:group_id])
    @catalogs = BookCatalog.where(:group_id => [0, session[:group_id]])
  end

  # 図書詳細情報画面
  def show_detail
    flash[:book_code] = params[:book_code]
    flash[:group_id] = params[:group_id]
    redirect_to :action => 'detail'
  end

  # 図書の詳細情報画面ページの表示
  def detail
    @books = Book.find_all_by_group_id_and_book_code_and_delete_date(session[:group_id], flash[:book_code], nil)
    @catalogs = BookCatalog.find_by_book_code_and_group_id(flash[:book_code], [0, flash[:group_id]])
    flash[:book_code] = flash[:book_code]
    render 'detail'
  end

  # 図書削除機能
  # @param [Fixnum] book_to_delete 削除する図書ID
  # @return [View] マネジメントページへ飛ぶ
  def delete
    if Book.find(params[:book_to_delete]).delete_date == nil
      Book.find_by_id(params[:book_to_delete]).update_attributes(:delete_date => Time.now.strftime("%Y-%m-%d %H:%M:%S"))
    end

    redirect_to :action => 'management'
  end


  # 図書の登録
  # @param [String] :book_code 登録する図書コード
  # @param [Fixnum] :group_id 登録するグループID
  # @param [Fixnum] :user_id 登録するユーザID
  # @param [Fixnum] :qr 登録する本棚ID
  # @return [String] 登録した結果を含むJSON形式の文字列
  def create
    # 複数のbook_codeを取得
    book_codes = params[:book_code]
    book_count = 0
    group_id = params[:group_id]
    shelf_name = ""

    if params[:qr].include?(",")
      params[:qr] = params[:qr].split(",")[1]
    end

    # グループに所属しているかどうか
    unless Member.find_by_user_id_and_group_id(params[:user_id], group_id)
      render :json => regist_json(params[:qr], 0)
      return
    end

    # 本棚が登録されていなければshelf_idを0に
    if qr = Shelf.find_by_shelf_id_and_group_id(params[:qr], group_id)
      qr = qr.shelf_id
      shelf_name = Shelf.find_by_shelf_id_and_group_id(qr,group_id).name
    else
      qr = 0
      shelf_name = ""
    end

    # book_codeの数だけ実行
    book_codes.each do |book_code|
      # 図書カタログが登録されていなければスキップ
      bc = BookCatalog.find_by_book_code_and_group_id(book_code, [0, params[:group_id]])
      if bc.nil?
        next
      end

      ActiveRecord::Base.transaction do
        # 図書の登録
        book = Book.new(:group_id => group_id, :book_code => book_code, :shelf_id => qr)
        book.save!
        book_count += 1
      end
    end
    render :json => regist_json(shelf_name, book_count)
  end

  # 図書の検索
  # @param [String] title 検索する図書名
  def search
    keyword = params[:keyword].strip
    user_id = session[:user_id]

    @result = []
    if keyword == ""
      render 'search'
      return
    end

    group_ids = Member.where(:user_id => user_id).pluck(:group_id)

    @books = ""
    @books = Book.where(:group_id => group_ids, :delete_date => nil)

    @catalogs = BookCatalog.search(:title_or_authors_or_publisher_cont => StringEscape.new.escape_like(keyword)).result

    catalogs_code = @catalogs.pluck(:book_code)
    @result = @books.search(:book_code_cont_any => catalogs_code).result
    if @result.to_sql == @books.to_sql
      @result = []
    end

    flash[:keyword] = keyword
    render 'search'
  end

  # 該当する図書の本棚を登録する
  # @param [String] book_code 図書コード
  # @param [Fixnum] group_id グループID
  # @param [Fixnum] shelf_id 本棚ID
  def update_shelf_id
    book_code = params[:book_code]
    group_id = params[:group_id]
    shelf_id = params[:shelf_id]
    book_id = nil

    other_log = nil
    books = Book.find_all_by_book_code_and_group_id_and_delete_date(book_code, group_id, nil)
    books.each do |book|
      other_log = LendLog.find_by_book_id_and_temporary(book.id, true)
      if other_log
        break
      end
    end

    # 一時借用のため本棚登録をする必要がある場合
    if other_log
      other_log.temporary = false
      other_log.save!
      book_id = other_log.book_id
    # 本棚IDが0のため本棚登録をする必要がある場合
    elsif
      book_id = Book.find_by_book_code_and_shelf_id_and_group_id(book_code, 0, group_id)
    end

    book = Book.find(book_id)
    book.shelf_id = shelf_id
    book.save!
    render :json => LendLogController.new.array_to_json(0, book_code)
  end



  # 登録時のJSON作成
  # @param [Fixnum] qr 登録する本棚ID
  # @param [Fixnum] book_num 登録する図書の数
  # @return [String] 本棚ID, 図書の数が含まれるJSON形式の文字列
  def regist_json(qr, book_num)
    json_array = {}
    result_array = Array.new
    result_array[0] = {"qr" => "#{qr}"}
    result_array[1] = {"book_num" => "#{book_num}"}
    json_array["result"] = result_array
    result = JSON.generate(json_array)
    return result
  end

end
