class RoundtablePresenter < ActiveRecord::Base
  belongs_to :roundtable
  belongs_to :user

  validates :user_id, uniqueness:  { scope: :roundtable_id }
end