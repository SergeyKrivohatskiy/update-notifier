# encoding: UTF-8
require 'httparty_wrapper'

module DatabaseHelper

  @resources = [Resource.new(name: 'Гогле', url: 'http://google.ru', tags: %w[search, favorite, GDG]),
                Resource.new(name: 'Яndex', url: 'http://yandex.ru', tags: %w[search]),
                Resource.new(name: 'Thumbtack', url: 'http://thumbtack.net', tags: %w[favorite it development]),
                Resource.new(name: 'ИСС Арт', url: 'http://issart.ru', tags: %w[it development])]
  #class Resource
  #  attr_accessor :url, :tags, :name
  #
  #  def initialize(name, url, tags)
  #    @name = name
  #    @url = url
  #    @tags = tags
  #  end
  #end

  def self.sign_in(email)
    email = 'example@mail.com'
    response = HTTPartyWrapper::get('users/signin', { email: email })
    response.parsed_response
    #2
  end

  def self.resources(user_id)
    # TODO It's stub. It receive json with resources and parse it to collection (Hash ?)
    # Structure of response:
    # [
    #   {
    #     name: %resource_1_name%,
    #     url: %resource_1_url%,
    #     tags: [ tag_1, tag_2, tag_3]
    #   },
    #   ...
    #   {
    #     name: %resource_N_name%,
    #     url: %resource_N_url%,
    #     tags: [ tag_1, tag_2, tag_3]
    #   }
    # ]
    #
    response = HTTPartyWrapper::get("users/#{user_id}/resources", nil)
    p response.parsed_response
    return @resources
  end

  def self.edit_resource(user_id, resource_id, name, url, tags)
    # TODO It's stub. It, I think, should return boolean value - result of updating. Maybe error code
    # TODO Передавать ли дату? (пока ресурс дойдёт до базы, пройдёт время)
    resource = Resource.new(name: name, url: url, tags: tags)
    if resource.valid?
      #response = HTTPartyWrapper::post('resource', user_id, id: resource_id, name: name, url: url,
      #                tags: tags)
      @resources.push Resource.new(name: name, url: url, tags: tags)
    end
    resource.errors.full_messages
  end

  def self.delete_resource(resource_id)
    # TODO It' stub. Return boolean ?
    #response = HTTPartyWrapper::delete('resource', resource_id)
    rand(0..1) == 0
  end

end