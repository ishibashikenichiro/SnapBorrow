# -*- coding: utf-8 -*-
class InvitationMailer < ActionMailer::Base
  # Subject can be set in your I18n file at config/locales/en.yml
  # with the following lookup:
  #
  #   en.invitation_mailer.sendmail_invite.subject
  #
  def sendmail_invite(address, url_hash)
    @url_hash = url_hash

    @group = Group.find(InvitationLog.find_by_url_hash(url_hash).group_id)

    mail to: address, subject: "SnapBorrowからのお知らせ", from: "SnapBorrow <info@snapborrow.net>"
  end
end
