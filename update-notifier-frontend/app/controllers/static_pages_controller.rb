class StaticPagesController < ApplicationController
  skip_before_filter :require_login, only: [:home, :signin]

  def home
  end

  def signin
    session[:email] = session[:email] || params[:email]
    #params[:email] = 'cthutq66a@yandex.ru'
    id = DatabaseHelper.sign_in(params[:email]).to_i
    if id > 0
      session[:user_id] = id
      redirect_to resources_path
    else
      render :home
    end
  end

  def about
  end
end
