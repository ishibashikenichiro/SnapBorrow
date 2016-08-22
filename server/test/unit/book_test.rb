require 'test_helper'

class BookTest < ActiveSupport::TestCase
  test "should create new Book" do
    assert(Book.create(
                       :book_code => "1234567890123",
                       :group_id => 1,
                       :shelf_id => 1), "create failed")
  end

  test "should create Book dupulicate book_code" do
    assert(Book.create(
                       :book_code => "1111111111111",
                       :group_id => 1,
                       :shelf_id => 1), "create failed")
  end


  test "should create Book dupulicate group_id" do
    assert(Book.create(
                       :book_code => "1234567890123",
                       :group_id => 1,
                       :shelf_id => 3), "create failed")
  end

  test "should create Book dupulicate shlef_id" do
    assert(Book.create(
                       :book_code => "1234567890123",
                       :group_id => 2,
                       :shelf_id => 2,
                       ), "create failed")
  end

  test "should create Book dupulicate group_id and shelf_id" do
    assert(Book.create(
                       :book_code => "1234567890123",
                       :group_id => 1,
                       :shelf_id => 1), "create failed")
  end

  test "should create Book dupulicate book_code, group_id and shelf_id" do
    assert(Book.create(
                       :book_code => "1111111111111",
                       :group_id => 1,
                       :shelf_id => 1), "create failed")
  end


  test "should get Book count" do
    assert_equal(5, Book.count)
  end
end
