require 'test_helper'

class ShelfTest < ActiveSupport::TestCase
  test "should create new Shelf" do
    assert(Shelf.create(
                        :shelf_id => 3,
                        :group_id => 1,
                        :name => "shelf4"))
  end

  test "should create Shelf dupulicate shelf_id " do
    assert(Shelf.create(
                        :shelf_id => 2,
                        :group_id => 2,
                        :name => "shelf4"))
  end

  test "should create Shelf dupulicate group_id " do
    assert(Shelf.create(
                        :shelf_id => 3,
                        :group_id => 1,
                        :name => "shelf4"))
  end

  test "should not create Shelf dupulicate shelf_id and group_id" do
    assert_raise(ActiveRecord::RecordNotUnique){
      Shelf.create(
                        :shelf_id => 1,
                        :group_id => 1,
                        :name => "shelf4")}
  end



  test "should not create Shelf without Group" do
    assert_raise(ActiveRecord::InvalidForeignKey){
      Shelf.create(
                        :shelf_id => 1,
                        :group_id => 10,
                        :name => "shelf4")}
  end
end
