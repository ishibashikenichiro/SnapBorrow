class ErrorController < ApplicationController
  def error_report_form
  end

  def error_report
    detail = params[:detail]
    user_id = session[:user_id]
    ActiveRecord::Base.transaction do
      error_report = ErrorReport.new(:detail => detail, :user_id => user_id)
      error_report.save!
    end

    redirect_to '/'
  end
end
