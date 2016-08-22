  class StringEscape
    def escape_like(str)
      str.gsub(/[\\%_]/){|m| "\\#{m}"}
    end
  end
