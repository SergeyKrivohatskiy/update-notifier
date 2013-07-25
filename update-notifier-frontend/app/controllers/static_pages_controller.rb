class StaticPagesController < ApplicationController
  skip_before_filter :require_login, only: [:home, :signin]

  def home
  end

  def signin
    #session[:email] = session[:email] || params[:email]
    session[:user_id] = DatabaseHelper.sign_in(params[:email])
    redirect_to index_path
  end

  def help
  end

  def contact
  end
end
