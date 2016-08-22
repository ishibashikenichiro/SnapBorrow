# -*- coding: utf-8 -*-
class ShelfController < ApplicationController
  # 本棚一覧
  def index
    @shelves = Shelf.find_all_by_group_id(session[:group_id])
    @each_shelf_book_num = {}
    @shelves.each do |shelf|
      group_books = Book.find_all_by_group_id_and_shelf_id_and_delete_date(session[:group_id], shelf.shelf_id, nil)
      @each_shelf_book_num["#{shelf.shelf_id}"] = group_books.size
    end
  end

  # 本棚の詳細
  def show
    unless Shelf.find_by_group_id_and_shelf_id(session[:group_id], params[:id])
      redirect_to '/' and return
    end

    @shelf = Shelf.find_by_group_id_and_shelf_id(session[:group_id], params[:id])
    @shelves = Shelf.where(:group_id => session[:group_id]).where('shelf_id not in (?)', params[:id])
    @shelf_qr = "qr/qr_" + @shelf.group_id.to_s + "_" + @shelf.shelf_id.to_s
    @books = Book.find_all_by_group_id_and_shelf_id_and_delete_date(session[:group_id], @shelf.shelf_id, nil)
    @catalogs = BookCatalog.where(:group_id => [0, session[:group_id]])
  end

  # 本棚の登録
  # @param [String] shelf_name 本棚名
  def create
    if params[:shelf_name].size > 40 || params[:shelf_name].size < 1
      flash[:message] = "本棚名のフォーマットが間違っています。"
      render 'new' and return
    end

    group_id = session[:group_id]
    shelf = Shelf.find_all_by_group_id(group_id)
    if !shelf.empty?
      shelf_id = Shelf.find_all_by_group_id(group_id).last.shelf_id.to_i + 10
    else
      shelf_id = 1
    end

    shelf_name = params[:shelf_name]

    ActiveRecord::Base.transaction do
      shelf = Shelf.new(:shelf_id => shelf_id, :group_id => group_id, :name => shelf_name)
      shelf.save!
    end

    flash[:message] = "本棚「" + shelf_name + "」を登録しました。"
    @shelf = Shelf.find_by_group_id_and_shelf_id(group_id, shelf_id)
    @shelf_qr = "qr/qr_" + @shelf.group_id.to_s + "_" + @shelf.shelf_id.to_s
    redirect_to :action => 'show', :id => shelf_id
  end

  # 図書の本棚変更機能
  def change_shelf
    group_id = session[:group_id]
    shelf_id = params[:shelf_id]

    flash[:shelf_message] = ""
    books = Book.find_all_by_id_and_delete_date(params[:books], nil)
    ActiveRecord::Base.transaction do
      books.each do |book|
        book.shelf_id = shelf_id
        book.save!
        flash[:shelf_message] += "「#{BookCatalog.find_by_book_code_and_group_id(book.book_code, [0, group_id]).title}」，"
      end
      flash[:shelf_message] += "を「#{Shelf.find([shelf_id, group_id]).name}」に移動しました。"
      if books.size == 0
        flash[:shelf_message] = "図書が選択されていません。"
      end
    end
    @shelves = Shelf.where(:group_id => group_id).where('shelf_id not in (?)', params[:id])
    @books = Book.find_all_by_group_id_and_shelf_id_and_delete_date(group_id, params[:id], nil)

    redirect_to :action => 'show', :id => params[:id]
  end

  # QRコード画像の生成
  # @param [Fixnum] shelf_id 本棚ID
  # @param [Fixnum] group_id グループID
  def create_qr(shelf_id, group_id)
    shelf = Shelf.find_by_shelf_id_and_group_id(shelf_id, group_id)
    group_name = Group.find(group_id).name
    shelf_name_size = shelf.name.size
    group_name_size = group_name.size

    # QRコード自体の大きさ
    width = 50
    height = 50

    # フォントサイズ
    fontsize = 20

    # 文字の位置
    group_y = 20
    shelf_y = 45

    # ファイル名
    raw_png_name = "tmp/qr_#{group_id.to_s}_#{shelf_id.to_s}_raw.png"

    text = group_id.to_s + "," + shelf_id.to_s

    # QRコード画像の生成
    qr = RQRCode::QRCode.new(text, :size => 1, :level => :h )
    png = qr.to_img
    png.resize(width, height).save(raw_png_name)

    # 文字のサイズ分を右に伸ばす
    if group_name_size > shelf_name_size
      width += (group_name_size) * fontsize + width
    else
      width += (shelf_name_size) * fontsize + width
    end

    # 文字表示の部分を拡張
    image = Magick::Image.read(raw_png_name).first
    @image_raw = image
    image = image.change_geometry("#{width}x#{height}") do |cols,rows,img|
      img.resize!(cols, rows)
      img.background_color = 'white'
      img.extent(width, height)
    end

    # グループ名の表示
    draw = Magick::Draw.new
    draw.annotate(image, 0, 0, 50, group_y, group_name) do
      self.fill = '#2C7CFF'
      self.align = Magick::LeftAlign
      self.stroke = 'transparent'
      self.pointsize = fontsize
      self.text_antialias = true
      self.kerning = 1
      self.font = "./app/assets/fonts/ipaexg.ttf"
    end

    # 本棚名の表示
    draw.annotate(image, 0, 0, 50, shelf_y, shelf.name) do
      self.fill = '#FF367F'
      self.align = Magick::LeftAlign
      self.stroke = 'transparent'
      self.pointsize = fontsize
      self.text_antialias = true
      self.kerning = 1
      self.font = "./app/assets/fonts/ipaexg.ttf"
    end

    @image = image
    File.unlink(raw_png_name)
  end

  def get_qr
    create_qr(params[:id], session[:group_id])
    send_data(@image_raw.to_blob, :disposition => "inline", :type => "image/png")
  end

  def get_qr_with_name
    create_qr(params[:id], session[:group_id])
    send_data(@image.to_blob, :disposition => "inline", :type => "image/png")
  end
end
