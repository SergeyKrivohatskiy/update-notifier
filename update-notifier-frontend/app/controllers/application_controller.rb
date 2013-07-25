class ApplicationController < ActionController::Base
  protect_from_forgery
  before_filter :require_login

  def require_login
    unless session[:user_id]
      redirect_to(root_path, status: :see_other)
    end
  end
end
