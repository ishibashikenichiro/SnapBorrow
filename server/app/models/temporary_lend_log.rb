class TemporaryLendLog < ActiveRecord::Base
  attr_accessible :book_code, :group_id, :lend_date, :return_date, :status, :user_id, :lenders, :possessors
end
