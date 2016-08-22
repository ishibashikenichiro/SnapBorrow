# -*- coding: utf-8 -*-
class ApplicationController < ActionController::Base
  protect_from_forgery
  def get_string_width(str)
    hankaku_len = str.each_char.count {|x| x.ascii_only? }
    return hankaku_len + (str.size - hankaku_len) * 1.5
  end

end
