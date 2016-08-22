require 'test_helper'

class BookControllerTest < ActionController::TestCase
  def setup
    @controller = BookController.new
    @response = ActionController::TestResponse.new
    @request = ActionController::TestRequest.new
    @request.session[:user_id] = 1
    @request.session[:group_id] = 1
  end


  test "should get books belong to the group" do
    get :index
    assert_response :success
    assert_not_nil assigns(:books)
    assert_equal assigns(:books).size, 3
    Book.where(:group_id).each_with_index do |book, index|
      assert_equal assigns(:books)[index], book
    end
  end

  test "should return json object" do
    json = @controller.regist_json(1,1)
    assert_equal json, "{\"result\":[{\"qr\":\"1\"},{\"book_num\":\"1\"}]}"
  end

  test "should create a book" do
    post :create, :book_code => ["1111111111111"], :qr => 1, :user_id => 1, :group_id => 1
    assert_response :success
    book = @response.body
    res_json = @controller.regist_json("shelf1",1)
    assert_equal book, res_json
  end


  test "should not create a book have book_code not include in Book_Catalog" do
    post :create, :book_code => ["4444444444444"], :qr => 1, :user_id => 1, :group_id => 1
    assert_response :success
    book = @response.body
    res_json = @controller.regist_json("shelf1", 0)
    assert_equal book, res_json
  end

  test "should create a book have shelf_id not include in Shelf" do
    post :create, :book_code => ["1111111111111"], :qr => 3, :user_id =>1, :group_id => 1
    assert_response :success
    book = @response.body
    res_json = @controller.regist_json("", 1)
    assert_equal book, res_json
  end

  test "should not create a book have user_id not include in User" do
    post :create, :book_code => ["1111111111111"], :qr => 1, :user_id => 10, :group_id => 1
    assert_response :success
    book = @response.body
    res_json = @controller.regist_json(1, 0)
    assert_equal book, res_json
  end
end
