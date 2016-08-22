require 'rails_helper'

RSpec.describe BookController, :type => :controller do

  describe "GET 'management'" do
    it "returns http success" do
      get 'management'
      expect(response).to be_success
    end
  end

end
