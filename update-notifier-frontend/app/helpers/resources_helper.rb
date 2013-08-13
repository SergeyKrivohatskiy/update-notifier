module ResourcesHelper
  def clean_tags(tags_string)
    unless tags_string.empty?
      #tags_string.strip
      tags = tags_string.split(';').inject([]) do |tags, dirty_tag|
        if dirty_tag.strip.empty?
          tags
        else
          tags.push dirty_tag.strip
        end
      end
      tags
    else
      []
    end
  end
end
