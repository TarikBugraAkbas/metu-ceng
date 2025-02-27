:- module(main, [is_blogpost_written_by/2, has_post_in_topic/2, get_follower_count_of_blogger/2, get_post_count_of_topic/2, filter_posts_by_date/4, recommend_posts/2, recommend_bloggers/2]).
:- [kb].

% DO NOT CHANGE THE UPPER CONTENT, WRITE YOUR CODE AFTER THIS LINE

is_blogpost_written_by( BloggerNick , ShortPostName ) :-
    blogpost(PostID,ShortPostName, _, _),
    posted(BloggerNick, PostID).
    
has_post_in_topic(BloggerNick,Topic) :-
    blogpost(PostID,_,Topic,_), %get the postID under the topic
    posted(BloggerNick,PostID). %to find is he posted there

get_follower_count_of_blogger(BloggerNick, FollowerCount) :-
    findall(ReaderNick,follows(ReaderNick, BloggerNick),Followers),
    length(Followers, FollowerCount).
    
get_post_count_of_topic(Topic, PostCount) :-
    findall(PostID, blogpost(PostID, _, Topic, _),Posts),
    length(Posts, PostCount).
    
filter_posts_by_date(ListOfPostIDs, OldestDate, NewestDate, ListOfFilteredPostIDs) :-
    findall(PostID, (blogpost(PostID, _, _, ApprovedDate), ApprovedDate >= OldestDate, ApprovedDate =< NewestDate, member(PostID, ListOfPostIDs)), ListOfFilteredPostIDs). %Goals: Date between approved dates, PostID is member of ListOfPostIDs 
    
recommend_posts(ReaderNick , ListOfRecommendedPosts ) :-
    findall(PostID, (blogpost(PostID, Blogger , Topic , _), \+alreadyread(ReaderNick,PostID), reader(ReaderNick, Topics), member(Topic, Topics)), ListOfRecommendedPosts). %Goals: Not already written, and the topic is in reader's interest list.
    
recommend_bloggers( ReaderNick , ListOfRecommendedBloggers ) :- 
    findall(BloggerNick, (blogpost(_, _, Topic, _), has_post_in_topic(BloggerNick, Topic), reader(ReaderNick, Topics), member(Topic,Topics), \+follows(ReaderNick, BloggerNick)), UnSortedListOfRecommendedBloggers),
    sort(UnSortedListOfRecommendedBloggers, ListOfRecommendedBloggers). %Goals: Recommend blogger, which reader is not a follower of it, and who writes in reader's interest topics.
    