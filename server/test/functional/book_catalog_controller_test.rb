require 'test_helper'

class BookCatalogControllerTest < ActionController::TestCase
  def setup
    @controller = BookCatalogController.new
    @response = ActionController::TestResponse.new
    @request = ActionController::TestRequest.new
    @request.session[:user_id] = 1
    @request.session[:group_id] = 1
  end

  test "should return true from valid_date" do
    ret = @controller.valid_date("2000-01-01")
    assert_equal ret, true
  end

end
