# -*- coding: utf-8 -*-
require 'test_helper'

class SessionControllerTest < ActionController::TestCase
  def setup
    @controller = SessionController.new
    @request = ActionController::TestRequest.new
    @response = ActionController::TestResponse.new
  end

  test "should login as registered user with login_id" do
    post :create, :login_id => "user1", :password => "useruser1"
    assert_equal session[:user_id], 1
    assert_equal session[:group_id], 1
    assert_equal session[:manager], 1

    assert_response :redirect
  end

  # test "should login as registered user with e-mail" do
  #   post :create, :login_id => "user1@test.com", :password => "useruser1"
  #   assert_equal session[:user_id], 1
  #   assert_equal session[:group_id], 1
  #   assert_equal session[:manager], 1

  #   assert_response :redirect
  # end


  test "should not login as registered user with wrong password" do
    post :create, :login_id => "user1", :password => "testtest1"
    assert_equal session[:user_id], nil
    assert_equal session[:group_id], nil
    assert_response :redirect
  end

  test "should not login as registered user with blank password" do
    post :create, :login_id => "user1", :password => ""
    assert_equal session[:user_id], nil
    assert_equal session[:group_id], nil
    assert_equal session[:manager], nil

    assert_response :redirect
  end

  test "should not login as registered user with fullwidth space password" do
    post :create, :login_id => "user1", :password => "　"
    assert_equal session[:user_id], nil
    assert_equal session[:group_id], nil
    assert_equal session[:manager], nil

    assert_response :redirect
  end

  test "should not login as registered user with halfwidth space password" do
    post :create, :login_id => "user1", :password => " "
    assert_equal session[:user_id], nil
    assert_equal session[:group_id], nil
    assert_equal session[:manager], nil

    assert_response :redirect
  end

  test "should not login as not registered user" do
    post :create, :login_id => "test1", :password => "1234"
    assert_equal session[:user_id], nil
    assert_equal session[:group_id], nil
    assert_equal session[:manager], nil

    assert_response :redirect
  end

  test "should not login as blank username with blank password" do
    post :create, :login_id => "", :password => ""
    assert_equal session[:user_id], nil
    assert_equal session[:group_id], nil
    assert_equal session[:manager], nil

    assert_response :redirect
  end

  test "should not login as fullwidth space username with fullwidth space password" do
    post :create, :login_id => "　", :password => "　"
    assert_equal session[:user_id], nil
    assert_equal session[:group_id], nil
    assert_equal session[:manager], nil

    assert_response :redirect
  end

  test "should not login as halfwidth space username with halfwidth space password" do
    post :create, :login_id => " ", :password => " "
    assert_equal session[:user_id], nil
    assert_equal session[:group_id], nil
    assert_equal session[:manager], nil

    assert_response :redirect
  end

  test "should show login page" do
    get 'login'
    assert_equal session[:user_id], nil
    assert_equal session[:group_id], nil
    assert_equal session[:manager], nil

    assert_response :success
  end

  test "should logout" do
    post :create, :login_id => "user1", :password => "useruser1"
    assert_equal session[:user_id], 1
    assert_equal session[:group_id], 1
    assert_equal session[:manager], 1

    delete 'logout'
    assert_equal session[:user_id], nil
    assert_equal session[:group_id], nil
    assert_equal session[:manager], nil

    assert_response :redirect
  end

  test "should android login" do
    post 'android_login', :login_id => "user1", :password => "useruser1"
    assert_response :success
  end

  # TODO invitationlogがとれない
  # test "should login when invited" do
  #   flash[:invite] = true
  #   flash[:url_hash] = "invitation2"

  #   post :create, :login_id => "user4", :password => "user4"

  #   assert_equal InvitationLog.find(2), InvitationLog.find_by_url_hash(flash[:url_hash])
  #   assert_equal assigns(:group), Group.find(1)
  #   assert_equal session[:user_id], 4
  #   assert_equal session[:group_id], 0
  #   assert_equal session[:manager], nil
  #   assert_response :redirect
  # end

end
