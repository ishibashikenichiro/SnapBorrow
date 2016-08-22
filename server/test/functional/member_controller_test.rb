require 'test_helper'

class MemberControllerTest < ActionController::TestCase
  def setup
    @controller = MemberController.new
    @response = ActionController::TestResponse.new
    @request = ActionController::TestRequest.new
    @request.session[:user_id] = 1
    @request.session[:group_id] = 1
  end

  test "should get members belong to the group" do
    get :index
    assert_response :success
    assert_not_nil assigns(:members)
    assert_equal assigns(:members).size, 2
    assert_equal assigns(:members)[0], Member.find(1)
    assert_equal assigns(:members)[1], Member.find(2)
    assert_equal assigns(:members)[2], nil
  end
end
