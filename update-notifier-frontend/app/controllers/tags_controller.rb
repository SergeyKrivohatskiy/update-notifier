class TagsController < ApplicationController
  def tags
    #GET	/photos	index	отображает список всех фото
  end
#  GET	/photos/new	new	возвращает форму HTML для создания нового фото
#  POST	/photos	create	создает новое фото
#GET	/photos/:id	show	отображает определенное фото
  def edit
    #GET	/photos/:id/edit	edit	возвращает форму HTML для редактирования фото
    #PATCH/PUT	/photos/:id	update	обновляет определенное фото
  end

  def destroy
    #DELETE	/photos/:id	destroy	удаляет определенное фото
  end
end