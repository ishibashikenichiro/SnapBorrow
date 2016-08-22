# -*- coding: utf-8 -*-
BmsApp::Application.routes.draw do

 


  # ユーザの削除完了画面
  get 'user/delete_success' => 'user#delete_success'

  # ユーザの削除機能
  delete 'user' => 'user#delete'

  # ユーザの削除確認画面
  get 'user/delete' => 'user#delete_confirmation'

  # グループが解散する場合のグループ脱退機能
  delete 'member/breakup' => 'member#breakup'

  # グループが解散する場合の確認画面
  get 'group/breakup' => 'group#breakup'

  # グループからの脱退機能
  delete 'member/leave' => 'member#leave'

  # マネージャの任命機能
  post 'manager/assigne_manager' => 'manager#create'

  # 登録状態の問い合わせ機能
  post '/get_status' => 'user#get_status'

  # メンバの管理ページ
  get 'member/management' => 'member#management'

  # メンバの削除
  delete 'member' => 'member#delete'

  # 本棚移動機能
  post '/shelf/change_shelf/:id' => 'shelf#change_shelf'

  # 本棚QRコードの生画像の表示
  get '/shelf/get_qr' => 'shelf#get_qr'

  # 本棚QRコードの名前付き画像の表示
  get '/shelf/get_qr_with_name' => 'shelf#get_qr_with_name'

  # 手動図書カタログ登録機能
  post '/manually_catalog_register' => 'book_catalog#manually_catalog_register'

  # パスワードリセット申請ページ
  get '/forget_password' => 'user#forget_password_request'

  # パスワードリセットメール送信
  post '/forget_password' => 'user#forget_password'

  # パスワードリセットページ
  get '/password_reset' => 'user#password_reset_request'

  # パスワードリセット送信
  post '/password_reset' => 'user#password_reset'

  # 図書の管理画面
  get 'book/management' =>'book#management'

  # 図書詳細情報画面
  post 'book/detail' =>'book#show_detail'

  # 図書詳細情報のページ
  get 'book/detail' => 'book#detail'

  # 図書の削除機能
  delete '/book/delete' => 'book#delete'

  # メンバ招待ページ
  get '/member/invite' => 'member#invite'

  # メンバ招待成功ページ
  get '/member/invite_success' => 'member#invite_success'

  # メンバ招待承認ページ
  get '/member/invite_confirmation' => 'member#invite_confirmation'

  # グループの招待フォーム
  get '/group/invite' => 'group#invite'

  # グループ招待メールの送信
  post '/group/invite_mail' => 'group#invite_mail'

  # Webのログアウト
  delete '/logout' => 'session#logout'

  # 図書の本棚ID更新
  # @param [String] book_code 図書コード
  # @param [Fixnum] group_id グループ
  # @param [Fixnum] shelf_id 本棚ID
  post '/update_shelf_id' => 'book#update_shelf_id'

  # GCMの通知機能
  # @param user_id:int
  # @param book_code:string
  # @param group_id:int
  post '/gcm/notify' => 'gcm#notify'

  # GCMの登録ID割当機能
  # @param user_id:int
  # @param reg_id:string
  post 'gcm/register' => 'gcm#register_user_id'

  # GCMの登録ID解除機能
  # @param user_id
  # @param reg_id:string
  post 'gcm/unregister' => 'gcm#unregister_user_id'

  # 図書を所持している場合
  # @param [String] book_code
  # @param [Fixnum] group_id
  post 'lend_possess' => 'lend_log#push_possess'

  # 図書を所持していない場合
  # @param [String] book_code
  # @param [Fixnum] group_id
  post 'lend_not_possess' => 'lend_log#push_not_possess'

  # ユーザ登録画面
  match '/signup', to: 'user#new'

  # ユーザの登録確認画面へ
  # @param login_id:string
  # @param nickname:string
  # @param password:string
  # @param pasword_confirmation
  post '/signup_confirmation' => 'user#confirmation'

  # ユーザの登録
  # @param login_id:string
  # @param nickname:string
  # @param password:string
  post 'signup_register_user' => 'user#create'

  # ユーザ登録成功ページ
  get '/user/success' => 'user#create_success'

  # ユーザのプロフィール画面
  match '/profile' =>  'user#show_profile'

  # プロフィール編集画面
  # @param nickname:string
  # @param mail:string
  get '/profile_edit' => 'user#show_profile_edit'

  post '/profile_edit' => 'user#edit_profile'

  # パスワード変更画面
  get '/password_edit' => 'user#show_password_edit'

  post '/password_edit' => 'user#edit_password'

  #借用中図書一覧
  get '/borrowing' => 'user#show_borrowing'

  # webアプリケーションのログイン処理
  get '/login' => 'session#login'

  # webアプリケーションのログアウト処理
  delete '/logout' => 'session#logout'

  # webアプリケーションのホーム画面
  get '/session' => 'session#index'

  # Androidのログイン処理
  # @param login_id:string
  # @param password:string
  post '/android_login' => 'session#android_login'

  # Androidのログアウト処理
  # @param user_id:int
  # @param group_id:int
  post '/android_logout' => 'session#android_logout'

  # Androidの借用処理
  # @param user_id:int
  # @param group_id:int
  # @param book_code:string
  post '/lend_register' => 'lend_log#create'

  # Androidの一時借用処理
  # @param user_id:int
  # @param group_id:int
  # @param book_code:string
  post '/temporary_lend_register' => 'lend_log#temporary_create'

  # Androidの本棚選択借用処理
  # @param [Fixnum] qr
  # @param [String] book_code
  # @param [Fixnum] group_id
  # @param [Fixnum] user_id
  post '/select_shelf_lend' => 'lend_log#select_shelf_lend'

  # Androidの図書の登録処理
  # @param user_id:int
  # @param group_id:int
  # @param book_code:string
  post '/book_register' => 'book#create'

  # Androidの図書カタログの登録処理
  # @param book_code:string
  post '/catalog_register' => 'book_catalog#create'

  # グループの切り替え機能
  # @param [Fixnum] :group_id 切り替えるグループID
  post '/group/change' => 'group#change'

  # グループの切り替えページ
  get '/group/change_group' => 'group#change_group'

  # グループの新規作成ページ
  match '/group/new' => 'group#new'

  # グループ作成成功ページ
  get '/group/success' => 'group#create_success'

  # グループの一覧取得
  # @param [Fixnum] user_id 取得するユーザID
  post '/group/get_groups' => 'group#get_groups'

  # 図書検索機能
  # @param [String] title 検索する図書のタイトル
  post '/book/search' => 'book#search'

  #備品テスト
  get 'equipment/create'

  # エラーレポートフォーム
  get'/error_report_form' => 'error#error_report_form'
  post 'error_report' => 'error#error_report'

 match '/roundtable'=> 'roundtable#index'
  match '/roundtable/view'=> 'roundtable#view'
  post  'roundtable_view' => 'roundtable#view'
  post  'roundtable_create' => 'roundtable#create'
  match '/roundtable/createfrombook' => 'roundtable#createfrombook'

  post "roundtable_file_upload" => 'roundtable_file#upload'
  post "/roundtable_file/upload" => 'roundtable_file#upload'
  get "roundtable_file/download"
  get "roundtable_file/delete"
  match "roundtable/new_confirm" => 'roundtable#new_confirm'
  match "roundtable/new_save" => 'roundtable#new_save'

 match "roundtable/edit" => 'roundtable#edit'
 match "roundtable/edit_save" => 'roundtable#edit_save'
 post "roundtable/add_presenter" => 'roundtable#add_presenter'
 post "roundtable/delete_presenter" => 'roundtable#delete_presenter'

 match "equipment/oneupload"=>'equipment#oneupload'
 match "picupload/oneupload"=>'picupload#oneupload'

 post "/equipment/" => 'equipment#index_android'
 get  "/equipment/rec_img" => 'equipment#rec_img'
 match  "equipment/new" => 'equipment#new'
 match  "equipment/edit" => 'equipment#edit'
 put    "equipment/new" => 'equipment#updete'
 match  "equipment/borrow" => 'equipment#borrow'
 match  "equipment/show" => 'equipment#show'
 match  "equipment/logs" => 'equipment#logs'

#備品登録：備品情報
match "equipmentregistration/getjson" =>'equipment_registration#getjson'
#備品登録：学習画像
match "equipmentregistration/getpictures" =>'equipment_registration#getpictures'
match "check/equipmentcheck" =>'check#equipmentcheck'
  resources :book, :except => [:create]
  resources :group, :except => [:new, :index]
  resources :shelf
  resources :user, :except => [:create, :new, :edit, :index]
  resources :member
  resources :lend_log
  resources :session, :except => [:destroy]
  resources :roundtable
  resources :roundtable_file
  resources :equipment

 
  root :to => 'session#index'
  # The priority is based upon order of creation:
  # first created -> highest priority.

  # Sample of regular route:
  #   match 'products/:id' => 'catalog#view'
  # Keep in mind you can assign values other than :controller and :action

  # Sample of named route:
  #   match 'products/:id/purchase' => 'catalog#purchase', :as => :purchase
  # This route can be invoked with purchase_url(:id => product.id)

  # Sample resource route (maps HTTP verbs to controller actions automatically):
  #   resources :products

  # Sample resource route with options:
  #   resources :products do
  #     member do
  #       get 'short'
  #       post 'toggle'
  #     end
  #
  #     collection do
  #       get 'sold'
  #     end
  #   end

  # Sample resource route with sub-resources:
  #   resources :products do
  #     resources :comments, :sales
  #     resource :seller
  #   end

  # Sample resource route with more complex sub-resources
  #   resources :products do
  #     resources :comments
  #     resources :sales do
  #       get 'recent', :on => :collection
  #     end
  #   end

  # Sample resource route within a namespace:
  #   namespace :admin do
  #     # Directs /admin/products/* to Admin::ProductsController
  #     # (app/controllers/admin/products_controller.rb)
  #     resources :products
  #   end

  # You can have the root of your site routed with 'root'
  # just remember to delete public/index.html.
  # root :to => 'welcome#index'

  # See how all your routes lay out with 'rake routes'

  # This is a legacy wild controller route that's not recommended for RESTful applications.
  # Note: This route will make all actions in every controller accessible via GET requests.
  # match ':controller(/:action(/:id))(.:format)'
end
