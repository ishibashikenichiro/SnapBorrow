require 'test_helper'

class UserControllerTest < ActionController::TestCase
  def setup
    @controller = UserController.new
    @response = ActionController::TestResponse.new
    @request = ActionController::TestRequest.new
    @request.session[:user_id] = 1
    @request.session[:group_id] = 1
  end

  test "should show profile of user1" do
  end


end
