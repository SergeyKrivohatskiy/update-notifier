require 'spec_helper'

describe DatabaseHelper do

  before(:all) { @user_id = 0 }

  describe 'symbolize' do
    it 'should return array of hashes, as origin, but with symbol keys instead string' do
      array_of_hash = [
          { 'id1' => 'value 1', 'url1' => 'http://some.url' },
          { 'id2' => 'value 1', 'url2' => 'http://another.url' }
      ]
      result = DatabaseHelper.send(:symbolize, array_of_hash)
      array_of_hash.each_with_index do |hash, index|
        hash.each_pair do |key, value|
          result[index][key.to_sym].should == value
        end
      end
    end

    it 'should return empty array if argument is empty array' do
      DatabaseHelper.send(:symbolize, []).should == []
    end

    it 'should return empty array if argument is nil' do
      DatabaseHelper.send(:symbolize, nil).should == []
    end
  end

  describe 'hashize' do
    it 'should return empty hash on empty array' do
      DatabaseHelper.send(:hashize, []).should == {}
    end

    it 'should return empty hash on nil' do
      DatabaseHelper.send(:hashize, nil).should == {}
    end

    it 'should work rightly' do
      array_of_hashes = [
          { "id1" => 12, "name" => "name3" },
          {"id2" => 34, "name" => "name4"},
      ]
      result = DatabaseHelper.send(:hashize, array_of_hashes)

      hash = array_of_hashes[0]
      result[hash['id1']].should == hash['name3']

      hash = array_of_hashes[1]
      result[hash['id2']].should == hash['name4']
    end
  end

  describe 'signin' do
    it 'returns something like user id in the response' do
      user_id = DatabaseHelper.sign_in('mail@post.com')
      user_id.to_i.should be_an Integer
    end

  end

  describe 'resource addition' do
    before { @user_id = DatabaseHelper.sign_in('mail@post.com') }
    it 'will be success if adds resource' do
      resource = Resource.new( name: 'test resource', url: 'http://localhost:8080', user_id: @user_id, dom_path: '/')
      DatabaseHelper.add_resource(resource).should be_true
    end
  end

  pending 'resource deletion not tested' do
    #before { @user_id = DatabaseHelper.sign_in('mail@post.com') }
  end

  pending "resource editing not tested " do
  end

  pending 'tags adding not tested' do
  end

  pending 'other operations?' do

  end
end