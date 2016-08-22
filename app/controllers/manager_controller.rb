# -*- coding: utf-8 -*-
class ManagerController < ApplicationController
  def create
    user_id = params[:user_id]
    group_id = session[:group_id]

    ActiveRecord::Base.transaction do
      @manager = Manager.find_all_by_group_id(session[:group_id])
      Manager.create(:user_id => user_id, :group_id => group_id)
    end

    @members = Member.where(:group_id => session[:group_id])
    @users = User.where(:id => @members.pluck(:user_id))

    redirect_to :controller => "member", :action => "management"
  end
end
