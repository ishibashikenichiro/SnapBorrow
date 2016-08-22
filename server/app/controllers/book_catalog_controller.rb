# -*- coding: utf-8 -*-
class BookCatalogController < ApplicationController
  protect_from_forgery :except => ['create']

  # 図書カタログの作成
  # @param [Integer] :group_id グループID
  # @param [String] :book_code 登録する図書コード
  def create
    # 図書カタログがあるかどうか
    check_empty = BookCatalog.find_by_book_code_and_group_id(params[:book_code], [0, params[:group_id]])

    # 図書カタログがないならば登録
    if check_empty.nil?


      retry_count = 0
      begin
        books = GoogleBooks.search("isbn:#{params[:book_code]}")
      rescue
        sleep 0.1
        if retry_count < 5
          retry_count += 1
          retry
        else
          result = {"result" => "0"}
          render :json => result.to_json and return
        end
      end

      title = ""
      author = ""
      publisher = ""
      pub_date = ""
      image = ""

      if books.first.nil?
        result = {"result" => "0"}
        render :json => result.to_json and return
      end
      book = books.first

      # 取得した情報からDBに入れる情報を取得
      title = book.title
      author = book.authors
      publisher = book.publisher
      pub_date = book.published_date
      image = book.image_link
      # unless image.nil?
      #   image += "&zoom=3"
      # end

      # 出版日のフォーマットが良くない場合はnilにする
      if !valid_date(pub_date)
        pub_date = nil
      end

      # タイトルがとれなかったら0を返す
      if title.empty?
        result = {"result" => "0"}
        render :json => result.to_json and return
      end

      # 図書カタログの登録
      ActiveRecord::Base.transaction do
        bc = BookCatalog.new(:book_code => params[:book_code],
                             :title => title,
                             :authors =>author,
                             :publisher => publisher,
                             :publication_date => pub_date,
                             :image => image,
                             :group_id => 0)
        bc.save!
      end
    end
    result = {"result" => "1"}
    render :json => result.to_json
  end

  # 手動図書カタログ登録機能
  # @param [String] book_code 図書コード
  # @param [String] title タイトル
  def manually_catalog_register
    book_code = params[:book_code]
    title = params[:title]
    group_id = params[:group_id]

    ActiveRecord::Base.transaction do
      bc = BookCatalog.new(:book_code => book_code, :title => title, :group_id => group_id)
      bc.save!
    end
    result = {"result" => "1"}
    render :json => result.to_json
  end

  # dateの日付のフォーマット検証
  # @param [String] date 検証する日付
  # @return [boolean] 日付が妥当かどうか
  def valid_date(date)
    if date.nil?
      return false
    end
    begin
      d = Date.parse(date)
      return true
    rescue ArgumentError
      return false
    end
  end
end
