# encoding: UTF-8
require 'httparty_wrapper'
require 'webrick/httpstatus'

module DatabaseHelper

  #@resources = [Resource.new(name: 'Гогле', url: 'http://google.ru', tags: %w[search, favorite, GDG]),
  #              Resource.new(name: 'Яndex', url: 'http://yandex.ru', tags: %w[search]),
  #              Resource.new(name: 'Thumbtack', url: 'http://thumbtack.net', tags: %w[favorite it development]),
  #              Resource.new(name: 'ИСС Арт', url: 'http://issart.ru', tags: %w[it development])]

  def self.sign_in(email)
    response = HTTPartyWrapper::get('signin', { email: email })
    response.parsed_response
    #2
  end

  def self.resources(user_id)
    response = HTTPartyWrapper::get("#{user_id}/resources")
    symbolize(response.parsed_response)
    #return @resources
  end

  def self.add_resource(resource)
    # TODO It's stub. It, I think, should return boolean value - result of updating. Maybe error code
    if resource.valid?
      response = HTTPartyWrapper::post("#{resource.user_id}/resources", nil, resource)
      p response.parsed_response
      #@resources.push Resource.new(name: name, url: resource.url, tags: resource.tags)
    end
    resource.errors.full_messages
  end

  def self.edit_resource(user_id, resource_id, name, url, tags)
    # TODO It's stub. It, I think, should return boolean value - result of updating. Maybe error code
    resource = Resource.new(name: name, url: url, tags: tags)
    if resource.valid?
      #response = HTTPartyWrapper::post('resource', user_id, id: resource_id, name: name, url: url,
      #                tags: tags)
      @resources.push Resource.new(name: name, url: url, tags: tags)
    end
    resource.errors.full_messages
  end

  def self.delete_resource(user_id, resource_id)
    response = HTTPartyWrapper::delete("#{user_id}/resources/#{resource_id}", nil)
    WEBrick::HTTPStatus[response.code].kind_of? Succes
  end

  def self.tags(user_id)
    response = HTTPartyWrapper::get("#{user_id}/tags")
    hashize(response.parsed_response)
  end

  def self.add_tag(user_id, name)
    response = HTTPartyWrapper::post("#{user_id}/tags", nil, name)
    response.parsed_response
  end

  private
  def self.symbolize(array_of_hash)
    array_of_hash.map do |hash|
      hash.inject({}){|memo,(k,v)| memo[k.to_sym] = v; memo}
    end
  end

  def self.hashize(array_of_hash)
    hash = {}
    array_of_hash.each do |item|
      key = item.shift[1]
      value = item.shift[1]
      hash[key] = value
    end
    hash
  end

end