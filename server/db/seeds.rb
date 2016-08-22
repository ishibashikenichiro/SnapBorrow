# -*- coding: utf-8 -*-
# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rake db:seed (or created alongside the db with db:setup).
#
# Examples:
#
#   cities = City.create([{ name: 'Chicago' }, { name: 'Copenhagen' }])
#   Mayor.create(name: 'Emanuel', city: cities.first)
ActiveRecord::Base.transaction do

g1 = Group.create(:name => "Zimsy")
g2 = Group.create(:name => "CORE")
g3 = Group.create(:name => "チームS.O.Y")
g4 = Group.create(:name => "TAMs")
g5 = Group.create(:name => "SAR-AG")

u1 =  User.create(:login_id => "kyoyu", :nickname => "京遊 士多郎", :mail => "kyoyu@test.com", :password => "sendouteki", :password_confirmation => "sendouteki")
u2 =  User.create(:login_id => "taro1", :nickname => "太郎1", :mail => "taro1@test.com", :password => "sendouteki", :password_confirmation => "sendouteki")
u3 =  User.create(:login_id => "taro2", :nickname => "太郎2", :mail => "taro2@test.com", :password => "sendouteki", :password_confirmation => "sendouteki")
u4 =  User.create(:login_id => "taro3", :nickname => "太郎3", :mail => "taro3@test.com", :password => "sendouteki", :password_confirmation => "sendouteki")
u5 =  User.create(:login_id => "taro4", :nickname => "太郎4", :mail => "taro4@test.com", :password => "sendouteki", :password_confirmation => "sendouteki")
u6 =  User.create(:login_id => "taro5", :nickname => "太郎5", :mail => "taro5@test.com", :password => "sendouteki", :password_confirmation => "sendouteki")

u7 =  User.create(:login_id => "jiro1", :nickname => "二郎1", :mail => "jiro1@test.com", :password => "sendouteki", :password_confirmation => "sendouteki")
u8 =  User.create(:login_id => "jiro2", :nickname => "二郎2", :mail => "jiro2@test.com", :password => "sendouteki", :password_confirmation => "sendouteki")
u9 =  User.create(:login_id => "jiro3", :nickname => "二郎3", :mail => "jiro3@test.com", :password => "sendouteki", :password_confirmation => "sendouteki")
u10 =  User.create(:login_id => "jiro4", :nickname => "二郎4",:mail => "jiro4@test.com", :password => "sendouteki", :password_confirmation => "sendouteki")
u11 =  User.create(:login_id => "jiro5", :nickname => "二郎5",:mail => "jiro5@test.com", :password => "sendouteki", :password_confirmation => "sendouteki")

u12 =  User.create(:login_id => "saburo1", :nickname => "三郎1", :mail => "saburo1@test.com", :password => "sendouteki", :password_confirmation => "sendouteki")
u13 =  User.create(:login_id => "saburo2", :nickname => "三郎2", :mail => "saburo2@test.com", :password => "sendouteki", :password_confirmation => "sendouteki")
u14 =  User.create(:login_id => "saburo3", :nickname => "三郎3", :mail => "saburo3@test.com", :password => "sendouteki", :password_confirmation => "sendouteki")
u15 =  User.create(:login_id => "saburo4", :nickname => "三郎4", :mail => "saburo4@test.com", :password => "sendouteki", :password_confirmation => "sendouteki")
u16 =  User.create(:login_id => "saburo5", :nickname => "三郎5", :mail => "saburo5@test.com", :password => "sendouteki", :password_confirmation => "sendouteki")


Manager.create(:group_id => g1.id, :user_id => u2.id)

Manager.create(:group_id => g2.id, :user_id => u3.id)

Manager.create(:group_id => g3.id, :user_id => u4.id)

Manager.create(:group_id => g4.id, :user_id => u5.id)

Manager.create(:group_id => g5.id, :user_id => u6.id)

Member.create(:group_id => g1.id, :user_id => u1.id)
Member.create(:group_id => g2.id, :user_id => u1.id)
Member.create(:group_id => g3.id, :user_id => u1.id)
Member.create(:group_id => g4.id, :user_id => u1.id)
Member.create(:group_id => g5.id, :user_id => u1.id)


Member.create(:group_id => g1.id, :user_id => u2.id)
Member.create(:group_id => g1.id, :user_id => u7.id)
Member.create(:group_id => g1.id, :user_id => u12.id)

Member.create(:group_id => g2.id, :user_id => u3.id)
Member.create(:group_id => g2.id, :user_id => u8.id)
Member.create(:group_id => g2.id, :user_id => u13.id)

Member.create(:group_id => g3.id, :user_id => u4.id)
Member.create(:group_id => g3.id, :user_id => u9.id)
Member.create(:group_id => g3.id, :user_id => u14.id)

Member.create(:group_id => g4.id, :user_id => u5.id)
Member.create(:group_id => g4.id, :user_id => u10.id)
Member.create(:group_id => g4.id, :user_id => u15.id)

Member.create(:group_id => g5.id, :user_id => u6.id)
Member.create(:group_id => g5.id, :user_id => u11.id)
Member.create(:group_id => g5.id, :user_id => u16.id)

Shelf.create(:shelf_id => 1, :group_id => g1.id, :name => "3F817")
Shelf.create(:shelf_id => 11, :group_id => g1.id, :name => "3B311")
Shelf.create(:shelf_id => 21, :group_id => g1.id, :name => "技術書")
Shelf.create(:shelf_id => 31, :group_id => g1.id, :name => "漫画")

Shelf.create(:shelf_id => 1, :group_id => g2.id, :name => "3F817")
Shelf.create(:shelf_id => 11, :group_id => g2.id, :name => "3B311")
Shelf.create(:shelf_id => 21, :group_id => g2.id, :name => "技術書")
Shelf.create(:shelf_id => 31, :group_id => g2.id, :name => "漫画")

Shelf.create(:shelf_id => 1, :group_id => g3.id, :name => "3F817")
Shelf.create(:shelf_id => 11, :group_id => g3.id, :name => "3B311")
Shelf.create(:shelf_id => 21, :group_id => g3.id, :name => "技術書")
Shelf.create(:shelf_id => 31, :group_id => g3.id, :name => "漫画")

Shelf.create(:shelf_id => 1, :group_id => g4.id, :name => "3F817")
Shelf.create(:shelf_id => 11, :group_id => g4.id, :name => "3B311")
Shelf.create(:shelf_id => 21, :group_id => g4.id, :name => "技術書")
Shelf.create(:shelf_id => 31, :group_id => g4.id, :name => "漫画")

Shelf.create(:shelf_id => 1, :group_id => g5.id, :name => "3F817")
Shelf.create(:shelf_id => 11, :group_id => g5.id, :name => "3B311")
Shelf.create(:shelf_id => 21, :group_id => g5.id, :name => "技術書")
Shelf.create(:shelf_id => 31, :group_id => g5.id, :name => "漫画")





g1 =   Group.create(:name => "Group1")
g2 =   Group.create(:name => "Group2")
g3 =   Group.create(:name => "Group3")


u1 =  User.create(:login_id => "user1", :nickname => "User1", :mail => "user1@test.com", :password => "useruser1", :password_confirmation => "useruser1");
u2 =   User.create(:login_id => "user2", :nickname => "User2", :mail => "user2@test.com", :password => "useruser2", :password_confirmation => "useruser2");
u3 =   User.create(:login_id => "user3", :nickname => "User3",:mail => "user3@test.com", :password => "useruser3", :password_confirmation => "useruser3");
u4 =   User.create(:login_id => "user4", :nickname => "User4", :mail => "user4@test.com", :password => "useruser4", :password_confirmation => "useruser4");
u5 =   User.create(:login_id => "user5", :nickname => "User5", :mail => "user5@test.com", :password => "useruser5", :password_confirmation => "useruser5");
u6 =   User.create(:login_id => "user6", :nickname => "User6", :mail => "user6@test.com", :password => "useruser6", :password_confirmation => "useruser6");

  BookCatalog.create(:book_code => "1234567890123", :title => "book1", :authors => "author1", :publisher => "publisher1", :publication_date => Time.now.strftime("%Y-%m-%d %H:%M:%S"), :group_id => 0)
  BookCatalog.create(:book_code => "1234567890124", :title => "book2", :authors => "author2", :publisher => "publisher2", :publication_date => Time.now.strftime("%Y-%m-%d %H:%M:%S"), :group_id => 0)
  BookCatalog.create(:book_code => "1234567890125", :title => "book3", :authors => "author3", :publisher => "publisher3", :publication_date => Time.now.strftime("%Y-%m-%d %H:%M:%S"), :group_id => 0)
  BookCatalog.create(:book_code => "1234567890126", :title => "book6", :authors => "author6", :publisher => "publisher6", :publication_date => Time.now.strftime("%Y-%m-%d %H:%M:%S"), :group_id => 0)
  BookCatalog.create(:book_code => "1234567890127", :title => "book7", :authors => "author7", :publisher => "publisher7", :publication_date => Time.now.strftime("%Y-%m-%d %H:%M:%S"), :group_id => 0)
  BookCatalog.create(:book_code => "1234567890128", :title => "book7", :authors => "author7", :publisher => "publisher7", :publication_date => Time.now.strftime("%Y-%m-%d %H:%M:%S"), :group_id => 0)
  BookCatalog.create(:book_code => "1234567890129", :title => "book7", :authors => "author7", :publisher => "publisher7", :publication_date => Time.now.strftime("%Y-%m-%d %H:%M:%S"), :group_id => 0)


  Book.create(:book_code => "1234567890123", :group_id => g1.id, :shelf_id => 1, :state => false)
  Book.create(:book_code => "1234567890124", :group_id => g1.id, :shelf_id => 1, :state => false)
  Book.create(:book_code => "1234567890125", :group_id => g1.id, :shelf_id => 1, :state => false)
  Book.create(:book_code => "1234567890126", :group_id => g1.id, :shelf_id => 1, :state => false)
  Book.create(:book_code => "1234567890127", :group_id => g1.id, :shelf_id => 1, :state => false)
  Book.create(:book_code => "1234567890128", :group_id => g1.id, :shelf_id => 1, :state => false)
  Book.create(:book_code => "1234567890123", :group_id => g2.id, :shelf_id => 1, :state => false)
  Book.create(:book_code => "1234567890123", :group_id => g1.id, :shelf_id => 11, :state => false)
  Book.create(:book_code => "1234567890129", :group_id => g1.id, :shelf_id => 1, :state => false)


  Shelf.create(:shelf_id => 1, :group_id => g1.id, :name => "shelf1")
  Shelf.create(:shelf_id => 11, :group_id => g1.id, :name => "shelf2")
  Shelf.create(:shelf_id => 1, :group_id => g2.id, :name => "shelf1")

  Manager.create(:group_id => g1.id, :user_id => u1.id)
  Manager.create(:group_id => g2.id, :user_id => u2.id)

  Member.create(:group_id => g1.id, :user_id => u1.id)
  Member.create(:group_id => g2.id, :user_id => u2.id)
  Member.create(:group_id => g1.id, :user_id => u3.id)
  Member.create(:group_id => g1.id, :user_id => u4.id)
  Member.create(:group_id => g2.id, :user_id => u5.id)

end
