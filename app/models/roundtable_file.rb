class RoundtableFile < ActiveRecord::Base
  belongs_to :user
  belongs_to :roundtable
  attr_accessible :filename, :keywords, :title
end
