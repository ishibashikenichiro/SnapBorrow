# -*- coding: utf-8 -*-
class UserMailer < ActionMailer::Base
  def forget_password(address, url_hash)
    @user = User.find_by_mail(address)
    @url_hash = url_hash
    mail to: address, subject: "Bircle: パスワードリセット", from: "Bircle <info@bircle.net>"
  end
end
