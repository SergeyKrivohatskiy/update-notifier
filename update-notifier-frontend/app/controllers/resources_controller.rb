class ResourcesController < ApplicationController
  def create
    # Create resource
    redirect_to action: :show
  end

  def show
    # 'Index' page - list of all resources and options
    @id = session[:id]

  end

  def update
    # Update resource
    redirect_to action: :show
  end

  def delete
    # Delete resource
    redirect_to action: :show
  end
end
