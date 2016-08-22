class AddBookcodeToLendLog < ActiveRecord::Migration
  def change
    add_column :lend_logs, :book_code, :string, :null => false
  end
end
