require 'test_helper'

class LendLogControllerTest < ActionController::TestCase
  def setup
    @controller = LendLogController.new
    @response = ActionController::TestResponse.new
    @request = ActionController::TestRequest.new
    @request.session[:user_id] = 1
    @request.session[:group_id] = 1
  end

  test "should get lend_logs belong to the group" do
    get :index
    assert_response :success
    assert_not_nil assigns(:lend_logs)
    assert_equal assigns(:lend_logs).size, 2
  end
end
