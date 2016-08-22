require 'test_helper'

class LendLogTest < ActiveSupport::TestCase
  test "should create new Lend_log" do
    assert(LendLog.create(
                          :book_id => 3,
                          :user_id => 2,
                          :lend_date => "2014-01-01 12:00:00",
                          :book_code => 3333333333333))
  end

  test "should create Lend_log dupulicate book_id" do
    assert(LendLog.create(
                          :book_id => 1,
                          :user_id => 2,
                          :lend_date => "2014-01-01 12:00:00",
                          :book_code => 1111111111111))
  end

  test "should create Lend_log dupulicate user_id" do
    assert(LendLog.create(
                          :book_id => 3,
                          :user_id => 1,
                          :lend_date => "2014-01-01 12:00:00",
                          :book_code => 3333333333333))
  end

  test "should create Lend_log dupulicate book_id and user_id" do
    assert(LendLog.create(
                          :book_id => 1,
                          :user_id => 1,
                          :lend_date => "2014-01-01 12:00:00",
                          :book_code => 1111111111111))
  end


end
