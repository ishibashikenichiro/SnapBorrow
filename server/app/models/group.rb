class Group < ActiveRecord::Base
  has_many :member
  has_many :manager
  has_many :user, :through => :member
  has_many :shelf
  has_many :book
  has_many :roundtable
  attr_accessible :delete_date, :name
end
