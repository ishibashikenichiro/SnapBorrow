require 'test_helper'

class RoundtableFileControllerTest < ActionController::TestCase
  test "should get upload" do
    get :upload
    assert_response :success
  end

  test "should get download" do
    get :download
    assert_response :success
  end

  test "should get delete" do
    get :delete
    assert_response :success
  end

end
