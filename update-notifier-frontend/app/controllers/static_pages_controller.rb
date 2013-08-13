class StaticPagesController < ApplicationController
  skip_before_filter :require_login, only: [:home, :signin, :signin_error]

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
      redirect_to action: :signin_error
    end
  end

  def signin_error
  end

  def about
  end
end
