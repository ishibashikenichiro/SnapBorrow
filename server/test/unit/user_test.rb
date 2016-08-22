require 'test_helper'

class UserTest < ActiveSupport::TestCase
  test "should create! User" do
    assert(User.create!(
                    :login_id => "test1",
                    :nickname => "test1",
                    :mail => "test1@test.com",
                    :password => "12345678",
                    :password_confirmation => "12345678"),
           "create! failed")
  end

  test "should not create! User dupulicated login_id" do
    assert_raise(ActiveRecord::RecordNotUnique){
      User.create!(
                    :login_id => "user1",
                    :nickname => "test1",
                    :mail => "test1@test.com",
                    :password => "12345678",
                    :password_confirmation => "12345678")
    }
  end

  test " should create! User dupulicated nickname" do
    assert(User.create!(
                    :login_id => "test1",
                    :nickname => "user1",
                    :mail => "test1@test.com",
                    :password => "12345678",
                    :password_confirmation => "12345678"),
           "create! failed")
  end

  test " should create! User dupulicated name" do
    assert(User.create!(
                    :login_id => "test1",
                    :nickname => "test1",
                    :mail => "test1@test.com",
                    :password => "12345678",
                    :password_confirmation => "12345678"),
           "create! failed")

  end

  test "  should not  create! User dupulicated mail" do
    assert_raise(ActiveRecord::RecordInvalid){
      User.create!(
                    :login_id => "test1",
                    :nickname => "test1",
                    :mail => "user1@test.com",
                    :password => "12345678",
                    :password_confirmation => "12345678")
    }
  end

  test "  should not create! User too long login_id" do
    assert_raise(ActiveRecord::RecordInvalid){
      User.create!(
                    :login_id => "abcdefghijklmnopqrstu",
                    :nickname => "test1",
                    :mail => "user1@test.com",
                    :password => "12345678",
                    :password_confirmation => "12345678")
    }
  end

  test "  should not  create! User too long nickname" do
    assert_raise(ActiveRecord::RecordInvalid){
      User.create!(
                    :login_id => "test1",
                    :nickname => "abedefghijklmnopqrstuvwxyzabcdefghijklmno",
                    :mail => "user1@test.com",
                    :password => "12345678",
                    :password_confirmation => "12345678")
    }
  end

  test "  should not create! User too long password" do
    assert_raise(ActiveRecord::RecordInvalid){
      User.create!(
                    :login_id => "test1",
                    :nickname => "test1",
                    :mail => "user1@test.com",
                    :password => "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567",
                    :password_confirmation => "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567")
    }
  end



  test " equal Users count to 4" do
    assert_equal 4, User.count
  end

end
