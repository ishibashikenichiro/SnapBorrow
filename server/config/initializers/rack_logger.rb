module Rails
  module Rack
    class Logger < ActiveSupport::LogSubscriber
      # Add UserAgent
      def started_request_message(request)
         'Started %s "%s" at %s by %s' % [
          request.request_method,
          request.filtered_path,
          Time.now.to_default_s,
          request.env['HTTP_USER_AGENT'] ]
      end
    end
  end
end
