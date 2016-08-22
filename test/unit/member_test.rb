require 'test_helper'

class MemberTest < ActiveSupport::TestCase
  test "should create Member dupulicated group_id" do
    assert(Member.create(:group_id => 1, :user_id => 3),
           "create failed")
  end

  test "should create Member dupulicated user_id" do
    assert(Member.create(:group_id => 2, :user_id => 2),
           "create failed")
  end

  test "should not create Member dupulicated group_id and user_id" do
    assert(Member.create(:group_id => 1, :user_id => 1))
  end

  test "should not create Member have user_id not included in User" do
    assert_raise(ActiveRecord::InvalidForeignKey){
      Member.create(:group_id => 1, :user_id => 10)
    }
  end

  test "should not create Member have group_id not included in Group" do
    assert_raise(ActiveRecord::InvalidForeignKey){
      Member.create(:group_id => 10, :user_id => 1)
    }
  end


end
