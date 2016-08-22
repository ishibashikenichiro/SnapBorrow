source 'https://rubygems.org'

gem 'rails', '3.2.18'

# Bundle edge Rails instead:
# gem 'rails', :git => 'git://github.com/rails/rails.git'

gem 'mysql2'

# Gem for foreign key
gem 'foreigner'

# Gem for composite primary key
gem 'composite_primary_keys'

# Gem for googlebooks
gem 'googlebooks'


#Gem for heroku rails deflate
gem 'heroku_rails_deflate', :group => :production

# Gem for test
group :test do
  gem 'test-unit-notify'
  gem 'minitest-doc_reporter'
end


# Gem for spring
group :development, :test do
  gem 'spring'
end

#Gem for e-mail validation
gem 'validates_email_format_of'

# Gem for gcm notification
gem 'gcm'

# Gems used only for assets and not required
# in production environments by default.
group :assets do
  gem 'sass-rails',   '~> 3.2.3'
  gem 'coffee-rails', '~> 3.2.1'

  # See https://github.com/sstephenson/execjs#readme for more supported runtimes
  gem 'therubyracer', :platforms => :ruby

  gem 'uglifier', '>= 1.0.3'
end

gem 'jquery-rails'

gem 'bootstrap-sass', '~> 3.2.0'
gem 'font-awesome-sass'

# To use ActiveModel has_secure_password
gem 'bcrypt-ruby', '~> 3.0.0'


# To use Jbuilder templates for JSON
# gem 'jbuilder'

# Use unicorn as the app server
# gem 'unicorn'

# Deploy with Capistrano
# gem 'capistrano'

# To use debugger
# gem 'debugger'

# Gem for test coverage
gem 'simplecov', :require => false, :group => :test

#Gem for amazon api
gem 'amazon-ecs'

#Gem for readline
gem 'rb-readline'

#Gem for env file
gem 'dotenv-rails'

#Gem for document
gem 'yard', require: false

#Gem for qr code
gem 'rqrcode'
gem 'rqrcode_png'

# Gem for image edit
gem 'rmagick'

# Gem for search
gem 'ransack'

# Gem for debug
group :development do
  gem 'rails-footnotes'
end

group :development, :test do
  gem 'pry', '< 0.10.0'
  gem 'pry-rails'
  gem 'hirb'
  gem 'hirb-unicode'
#  gem 'pry-debugger'
  gem 'pry-doc'
end

group :development, :test do
  gem 'better_errors', '1.1.0'
  gem 'binding_of_caller'
end
