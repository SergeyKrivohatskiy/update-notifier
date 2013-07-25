class StaticPagesController < ApplicationController
  def home
  end

  def signin
    #session[:email] = session[:email] || params[:email]
    session[:id] = DatabaseHelper.sign_in(params[:email])
    redirect_to index_path
  end

  def help
  end

  def contact
  end
end
