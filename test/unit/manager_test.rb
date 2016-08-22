require 'test_helper'

class ManagerTest < ActiveSupport::TestCase
  test "should create Manager dupulicated group_id" do
    assert(Manager.create(:group_id => 1, :user_id => 2),
           "create failed")
  end

  test "should create Manager dupulicated user_id" do
    assert(Manager.create(:group_id => 2, :user_id => 1),
           "create failed")
  end

  test "should not create Manager dupulicated group_id and user_id " do
    assert_raise(ActiveRecord::RecordNotUnique){
      Manager.create(:group_id => 1, :user_id => 1)
    }
  end

  test "should not create Manager without User" do
    assert_raise(ActiveRecord::InvalidForeignKey){
      Manager.create(:group_id => 1, :user_id => 10)
    }
  end

  test "should not create Manager without Group" do
    assert_raise(ActiveRecord::InvalidForeignKey){
      Member.create(:group_id => 10, :user_id => 1)
    }
  end


end
