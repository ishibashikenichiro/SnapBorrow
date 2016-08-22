# -*- coding: utf-8 -*-
class User < ActiveRecord::Base
  has_secure_password
  has_many :manager
  has_many :member
  has_many :group, :through => :member
  has_many :lend_log
  has_many :roundtable_file
  has_many :roundtable_presenter
  has_many :roundtable, :through => :roundtable_presenter

  attr_accessible :delete_date, :login_id, :mail, :nickname, :password_digest, :password, :password_confirmation

  validates :password, :length => {:minimum => 8, :maximum => 256, :message => "8文字以上256文字以内にしてください"}, :allow_nil => true
  validates :login_id, :length => {:maximum => 16, :message => "16文字以内にしてください"}
  validates :nickname, :length => {:maximum => 40, :message => "40文字以内にしてください"}
  validates :mail, :email_format => {:message => "メールアドレスの形式が不適切です"}, :uniqueness => true, :allow_nil => true
end
