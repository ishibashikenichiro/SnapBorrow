class Roundtable < ActiveRecord::Base
  belongs_to :group
  has_many :roundtable_file
  has_many :roundtable_presenter		
  has_many :user, :through => :roundtable_presenter
  attr_accessible :book_code, :enddatetime, :keywords, :place, :startdatetime, :title
end
