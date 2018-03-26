# U-Uber

FEATURE LIST:

1) [5pts] Registration: Registration: a new user (either UD or UU) has to provide the appropriate information;
he/she can pick a login-name and a password. The login name should be checked for uniqueness.
DONE
2) [5pts] Reserve: After registration, a user can record a reservation to any UC (the same user may reserve
the same UC multiple times). Each user session (meaning each time after a user has logged into the system)
may add one or more reservations, and all reservations added by a user in a user session are reported to
him/her for the final review and confirmation, before they are added into the database.
3) [5pts] New UC: A user may record the details of a new UC, and may update the information regarding
an existing UC he/she owns.
4) [5pts] Rides: A user can record a ride with a UC (the same user may ride the same UC multiple times).
Each user session (meaning each time after a user has logged into the system) may add one or more rides,
and all rides added by a user in a user session are reported to him/her for the final review and confirmation,
before they are added into the database. Note that a user may only record a ride at a UC during a period
that the associated UD is available.
5) [5pts] Favorite recordings: Users can declare a UC as his/her favorite car to hire.
6) [5pts] Feedback recordings: Users can record their feedback for a UC. We should record the date, the
numerical score (0= terrible, 10= excellent), and an optional short text. No changes are allowed; only one
feedback per user per UC is allowed.
7) [5pts] Usefulness ratings: Users can assess a feedback record, giving it a numerical score 0, 1, or 2 (’useless’,
’useful’, ’very useful’ respectively). A user should not be allowed to provide a usefulness-rating for his/her
own feedbacks.
8) [5pts] Trust recordings: A user may declare zero or more other users as ‘trusted’ or ‘not-trusted’.
9) [20pts] UC Browsing: Users may search for UCs, by asking conjunctive queries on the category, and/or
address (at CITY or State level), and/or model by keywords (e.g. BMW, Toyota, F150). Your system should
allow the user to specify that the results are to be sorted (a) by the average numerical score of the feedbacks,
or (b) by the average numerical score of the trusted user feedbacks.
10) [7pts] Useful feedbacks: For a given UD, a user could ask for the top n most ‘useful’ feedbacks (from all
feedbacks given to UCs owned by this UD). The value of n is user-specified (say, 5, or 10). The ‘usefulness’
of a feedback is its average ‘usefulness’ score.
11) [8pts] UC suggestions: When a user records his/her reservations to a UC ‘A’, your system should give a
list of other suggested UCs. UC ‘B’ is suggested, if there exist a user ‘X’ that hired both ‘A’ and ‘B’. The
suggested UCs should be sorted on decreasing total rides count (i.e., most popular first); count only rides
by users like ‘X’.
12) [10pts] ‘Two degrees of separation’: Given two user names (logins), determine their ‘degree of separation’,
defined as follows: Two users ‘A’ and ‘B’ are 1-degree away if they have both favorited at least one common
UC; they are 2-degrees away if there exists an user ‘C’ who is 1-degree away from each of ‘A’ and ‘B’, AND
‘A’ and ‘B’ are not 1-degree away at the same time.

one degree:
adddy and adam are 1 degree away
(both fav 1)

two degree:
adam fav 1
nick fav 123

adddy favs both

adam and nick are 2 degrees away
13) [10pts] Statistics: At any point, a user may want to show
• the list of the m (say m = 5) most popular UCs (in terms of total rides) for each category,
• the list of m most expensive UCs (defined by the average cost of all rides on a UC) for each category
• the list of m highly rated UDs (defined by the average scores from all feedbacks a UD has received for
all of his/her UCs) for each category
14) [5pts] User awards: At random points of time, the admin user wants to give awards to the ‘best’ users;
thus, the admin user needs to know:
• the top m most ‘trusted’ users (the trust score of a user is the count of users ‘trusting’ him/her, minus
the count of users ‘not-trusting’ him/her)
• the top m most ‘useful’ users (the usefulness score of a user is the average ‘usefulness’ of all of his/her
feedbacks combined)




Username : 5530u47	
Password : u2ebdcje


